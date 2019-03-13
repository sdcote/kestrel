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

  }


  public String getQuery() {
    return transportBuilder.getQuery();
  }

  public TransportBuilder setQuery(String path) {
    return transportBuilder.setQuery(path);
  }

  public int getPort() {
    return transportBuilder.getPort();
  }

  public TransportBuilder setPort(int port) {
    return transportBuilder.setPort(port);
  }

  public String getHostname() {
    return transportBuilder.getHostname();
  }

  public String getPassword() {
    return transportBuilder.getPassword();
  }

  public TransportBuilder setPassword(String password) {
    return transportBuilder.setPassword(password);
  }

  public String getUsername() {
    return transportBuilder.getUsername();
  }

  public TransportBuilder setUsername(String username) {
    return transportBuilder.setUsername(username);

  }

  public String getScheme() {
    return transportBuilder.getScheme();
  }

  public TransportBuilder setScheme(String scheme) {
    return transportBuilder.setScheme(scheme);

  }

  public TransportBuilder setHost(String host) {
    return transportBuilder.setHost(host);

  }

  public int getConnectionTimeout() {
    return transportBuilder.getConnectionTimeout();
  }

  // timeout in milliseconds; zero for infinite
  public TransportBuilder setConnectionTimeout(int timeout) {
    return transportBuilder.setConnectionTimeout(timeout);
  }

  public TransportBuilder setURI(String uri) throws IllegalArgumentException {
    return transportBuilder.setURI(uri);
  }

  public TransportBuilder setURI(URI uri) throws IllegalArgumentException {
    return transportBuilder.setURI(uri);
  }


}
