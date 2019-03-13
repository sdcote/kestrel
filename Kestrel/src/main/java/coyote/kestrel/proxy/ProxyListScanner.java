package coyote.kestrel.proxy;

import coyote.loader.log.Log;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Scan the classpath for all occurrences of a proxy list and load those
 * classes into the class list.
 *
 * <p>This allows the developer to pre-populate the ProxyBuilder with classes
 * to use as service proxies and not have to define them in code.</p>
 */
public class ProxyListScanner {
  private static final String FILENAME = "proxylist.txt";


  /**
   * TODO: Make this work.
   *
   * @return a list of classes designated as service proxies by proxy list files found in the classpath.
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
                  Log.info("Found " + jentry.getName());
                  loadMap(retval,jentry.getName());
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
                  Log.info("Found " + zentry.getName());
                  loadMap(retval,zentry.getName());
                }
              }
            } catch (IOException e) {
              Log.warn("Class path entry '" + entry + "' is not a valid zip archive: " + e.getMessage());
            }
          } else {
            // TODO: perform a directory search
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


  private static void loadMap(Map<Class, Object> map, String filename) {
    // load the file with the classloader
    // scan the file for class names
    // for each class name
    //   load the class
    //   load an instance
    //   place both in map
  }
}