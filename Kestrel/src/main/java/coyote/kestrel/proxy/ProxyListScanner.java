package coyote.kestrel.proxy;

import coyote.commons.FileUtil;
import coyote.commons.StreamUtil;
import coyote.loader.log.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
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
            // TODO: perform a directory search...requires constructing fully qualified names
            Log.debug("Proxy list scanner looking in directory: " + file.getAbsolutePath());
            List<File> files = FileUtil.getFiles(file, true);
            for (File fentry : files) {
              if (fentry.getName().toLowerCase().endsWith(FILENAME)) {
                Log.debug("Found service proxy configuration file: " + fentry.getAbsolutePath());
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


  private static void loadList(Map<Class, Object> list, String filename) {
    Log.info("Found " + filename);
    // load the file with the classloader
    ClassLoader cloader = ClassLoader.getSystemClassLoader();

    InputStream in = cloader.getResourceAsStream(filename);
    if (in != null) {
      try {
        Reader reader = StreamUtil.getReader(in);
        StringBuilder textBuilder = new StringBuilder();
        int c = 0;
        while ((c = reader.read()) != -1) {
          textBuilder.append((char) c);
        }
        System.out.println(textBuilder.toString());
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        try {
          in.close();
        } catch (IOException ignore) {
        }
      }
    }

    // scan the file for class names
    //   try JSON
    //   try simple class per line
    // for each class name
    //   load the class
    //   create an instance for reflection
    //   place both in map
  }
}