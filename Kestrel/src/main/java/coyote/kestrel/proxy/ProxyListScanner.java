package coyote.kestrel.proxy;

import coyote.commons.FileUtil;
import coyote.commons.StreamUtil;
import coyote.commons.StringUtil;
import coyote.loader.cfg.Config;
import coyote.loader.log.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Scan the classpath for all occurrences of a proxy list and load those
 * classes into the class list.
 *
 * <p>This allows the developer to pre-populate the ClientRegistry with classes
 * to use as service proxies and not have to define them in code.</p>
 */
public class ProxyListScanner {
  private static final String FILENAME = "serviceproxy.cfg";


  /**
   * TODO: Make this work.
   *
   * @return a map of classes designated as service proxies by proxy list files found in the classpath.
   */
  public static Map<Class, Object> scan() {
    Map<Class, Object> retval = new HashMap<>();

    StringTokenizer st = new StringTokenizer(System.getProperty("java.class.path"), System.getProperty("path.separator"));
    while (st.hasMoreTokens()) {
      String entry = st.nextToken();
      File file = new File(entry);
      if (file.exists()) {
        if (file.canRead()) {
          if (entry.endsWith("jar")) {
            JarFile jarfile = null;
            try {
              jarfile = new JarFile(file);
              Log.trace("checking '" + entry + "'");
              for (Enumeration<JarEntry> en = jarfile.entries(); en.hasMoreElements(); ) {
                JarEntry jentry = en.nextElement();
                Log.trace("    '" + jentry.getName() + "' " + jentry.getCrc());
                if (jentry.getName().toLowerCase().endsWith(FILENAME)) {
                  loadList(retval, jentry.getName());
                }
              }
            } catch (IOException e) {
              Log.warn("Class path entry '" + entry + "' is not a valid java archive: " + e.getMessage());
            }
          } else if (entry.endsWith("zip")) {
            ZipFile zipfile = null;
            try {
              zipfile = new ZipFile(file);
              for (Enumeration<? extends ZipEntry> en = zipfile.entries(); en.hasMoreElements(); ) {
                ZipEntry zentry = en.nextElement();
                Log.trace("    '" + zentry.getName() + "' " + zentry.getCrc());
                if (zentry.getName().toLowerCase().endsWith(FILENAME)) {
                  loadList(retval, zentry.getName());
                }
              }
            } catch (IOException e) {
              Log.warn("Class path entry '" + entry + "' is not a valid zip archive: " + e.getMessage());
            }
          } else if (file.isDirectory()) {
            List<File> files = FileUtil.getFiles(file, true);
            for (File fentry : files) {
              if (fentry.getName().toLowerCase().endsWith(FILENAME)) {
                String partialname = subtractDirFromFile(file, fentry);
                partialname = partialname.replace('\\', '/');
                if (partialname.charAt(0) == '/') partialname = partialname.substring(1);
                loadList(retval, partialname);
              }
            }
          } else {
            Log.warn("Class path entry '" + entry + "' is not a valid zip archive or directory");
          }
        } else {
          Log.warn("Class path entry '" + entry + "' is not readable");
        }
      } else {
        Log.warn("Class path entry '" + entry + "' does not appear to exist on file system");
      }
    } // while more path entries

    return retval;
  }

  /**
   * Find the portion of the file which represents the namespace from which it should be loaded
   *
   * @param dir  the directory reference to remove from the file path
   * @param file the file within the directory
   * @return the relative path from the given directory
   */
  private static String subtractDirFromFile(File dir, File file) {
    String retval = "";
    if (dir != null) {
      String dirname = dir.getAbsolutePath();
      if (file != null) {
        String filename = file.getAbsolutePath();
        int x;
        int end = Math.min(dirname.length(), filename.length());
        for (x = 0; x < end; x++) {
          if (dirname.charAt(x) != filename.charAt(x)) {
            break;
          }
        }
        if (x < filename.length()) {
          retval = filename.substring(x);
        }
      }
    } else {
      if (file != null) {
        retval = file.getAbsolutePath();
      }
    }
    return retval;
  }


  /**
   * @param list
   * @param filename
   */
  private static void loadList(Map<Class, Object> list, String filename) {
    Log.info("Found " + filename);

    String data = loadFile(filename);
    if (StringUtil.isNotBlank(data)) {
      Map<Class, Config> classMap = loadConfig(data);

      for( Map.Entry<Class,Config> entry: classMap.entrySet()){
        Constructor<?> ctor = null;
        try {
          Class<?> clazz =entry.getKey();
          ctor = clazz.getConstructor();
          Object object = ctor.newInstance();
          list.put(clazz,object);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }

  private static Map<Class, Config> loadConfig(String data) {
    Map<Class, Config> retval = new HashMap<>();
    List<String> classes = new ArrayList<>();
    String[] lines = data.split("\\r?\\n");
    for (String line : lines) {
      if (StringUtil.isNotBlank(line) && !line.trim().startsWith("#")) {
        classes.add(line.trim());
      }
    }
    for (String line : classes) {
      try {
        Class<?> clazz = Class.forName(line);
        retval.put(clazz, null);
      } catch (ClassNotFoundException e) {
        Log.error("Proxy scanner could not load class: '" + line + "'");
      }
    }
    return retval;
  }


  private static String loadFile(String filename) {
    String retval = "";
    try {
      ClassLoader cloader = ClassLoader.getSystemClassLoader();
      if (cloader == null) throw new IllegalStateException("System classloader returned null");

      InputStream in = cloader.getResourceAsStream(filename);
      if (in != null) {
        try {
          Reader reader = StreamUtil.getReader(in);
          StringBuilder sb = new StringBuilder();
          int c = 0;
          while ((c = reader.read()) != -1) {
            sb.append((char) c);
          }
          retval = sb.toString();
        } catch (Exception e) {
          Log.warn("Could not read service proxy file from classpath: '" + filename + "' - Reason: " + e.getLocalizedMessage());
        } finally {
          try {
            in.close();
          } catch (IOException ignore) {
          }
        }
      }
    } catch (Throwable ball) {
      Log.warn("Problems accessing classloader: " + ball.getLocalizedMessage());
    }
    return retval;
  }

}