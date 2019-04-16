package coyote;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;

/**
 * Wrapper around a properties object which is loaded from a specific "test.properties" file in the classpath.
 */
public class TestProperties {
  private static final Properties PROPERTIES = new Properties();

  private static final String TEST_PROPERTIES_FILE = "test.properties";

  static {
    // Load the properties
    try {
      PROPERTIES.load(TestProperties.class.getClassLoader().getResourceAsStream(TEST_PROPERTIES_FILE));
    } catch (IOException e) {
      System.err.println("Could not read in test properties file: " + e.getMessage());
    }
  }

  private TestProperties() {
    // no instances of this class
  }

  public static String getProperty(String key) {
    return PROPERTIES.getProperty(key);
  }

  public static String getProperty(String key, String defaultValue) {
    return PROPERTIES.getProperty(key, defaultValue);
  }

  public static Enumeration<?> propertyNames() {
    return PROPERTIES.propertyNames();
  }

  public static Set<String> stringPropertyNames() {
    return PROPERTIES.stringPropertyNames();
  }

  public static void list(PrintStream out) {
    PROPERTIES.list(out);
  }

  public static void list(PrintWriter out) {
    PROPERTIES.list(out);
  }

  public static Object setProperty(String key, String value) {
    return PROPERTIES.setProperty(key, value);
  }


}
