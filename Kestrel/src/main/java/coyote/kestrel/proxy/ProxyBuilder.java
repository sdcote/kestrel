package coyote.kestrel.proxy;

import java.util.ArrayList;
import java.util.List;

/**
 * Sources of inspiration:
 * http://software.clapper.org/javautil/api/org/clapper/util/classutil/ClassFinder.html
 * https://stackoverflow.com/questions/347248/how-can-i-get-a-list-of-all-the-implementations-of-an-interface-programmatically
 * <p>
 * Reflection is too slow and resource intensive. A service registry approach is more efficient.
 */
public class ProxyBuilder {
  /**
   * The list of classes we search for implementations.
   */
  private static final List<Class> proxyClasses = new ArrayList<>();

  static{
    // TODO: search classpath for lists of proxy classes to load "proxylist.json" for each one, load the classes here
  }

  private ProxyBuilder() {
    // no instances of this class
  }

  // other attributes to refine transport creation

  /**
   * Build a proxy of the given type connected to the messaging transport currently configured/
   *
   * @param type
   * @param <E>
   * @return the first type which implements the given interface type
   */
  public static <E> E build(Class<E> type) {

    // search for a class which implements the type

    // If that class also implements the KestrelProxy interface, configure it

    return type.cast(null);
  }


  /**
   * Add a class the builder should use to scan for proxy instances.
   *
   * @param proxyClass
   */
  public static void addProxyClass(Class proxyClass) {

  }
}
