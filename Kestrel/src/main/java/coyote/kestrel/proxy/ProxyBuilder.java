package coyote.kestrel.proxy;

import coyote.kestrel.transport.TransportBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a builder for creating and configuring service proxies.
 *
 * <p>Reflection is too slow and resource intensive. A service registry
 * approach is more efficient. This means the developer will need to register
 * proxy implementations with the builder, configure the builder, and then ask
 * for a configured proxy.</p>
 */
public class ProxyBuilder {
  /**
   * The list of classes we search for implementations.
   */
  private static final List<Class> proxyClasses;
  private static final Map<Class, Object> proxyCache = new HashMap<>();

  private static final TransportBuilder transportBuilder = new TransportBuilder();
  private static final ProxyBuilder instance = new ProxyBuilder();


  static {
    proxyClasses = ProxyListScanner.scan();
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
    proxyClasses.add(proxyClass);
  }

  public static ProxyBuilder setURI(String uri) throws IllegalArgumentException {
    ProxyBuilder.transportBuilder.setURI(uri);
    return instance;
  }

  public static ProxyBuilder setURI(URI uri) throws IllegalArgumentException {
    ProxyBuilder.transportBuilder.setURI(uri);
    return instance;
  }

  public static String getQuery() {
    return transportBuilder.getQuery();
  }

  public ProxyBuilder setQuery(String path) {
    ProxyBuilder.transportBuilder.setQuery(path);
    return instance;
  }

  public static int getPort() {
    return ProxyBuilder.transportBuilder.getPort();
  }

  public ProxyBuilder setPort(int port) {
    ProxyBuilder.transportBuilder.setPort(port);
    return instance;
  }

  public static String getHostname() {
    return ProxyBuilder.transportBuilder.getHostname();
  }

  public static String getPassword() {
    return ProxyBuilder.transportBuilder.getPassword();
  }

  public ProxyBuilder setPassword(String password) {
    ProxyBuilder.transportBuilder.setPassword(password);
    return instance;
  }

  public static String getUsername() {
    return ProxyBuilder.transportBuilder.getUsername();
  }

  public ProxyBuilder setUsername(String username) {
    ProxyBuilder.transportBuilder.setUsername(username);
    return instance;
  }

  public static String getScheme() {
    return ProxyBuilder.transportBuilder.getScheme();
  }

  public static ProxyBuilder setScheme(String scheme) {
    ProxyBuilder.transportBuilder.setScheme(scheme);
    return instance;
  }

  public static int getConnectionTimeout() {
    return ProxyBuilder.transportBuilder.getConnectionTimeout();
  }

  public ProxyBuilder setConnectionTimeout(int timeout) {
    ProxyBuilder.transportBuilder.setConnectionTimeout(timeout);
    return instance;
  }

  public ProxyBuilder setHost(String host) {
    ProxyBuilder.transportBuilder.setHost(host);
    return instance;
  }


}
