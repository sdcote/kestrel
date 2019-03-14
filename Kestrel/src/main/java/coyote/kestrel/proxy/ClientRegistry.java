package coyote.kestrel.proxy;

import coyote.kestrel.transport.Transport;
import coyote.kestrel.transport.TransportBuilder;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a builder for creating and configuring service proxies.
 *
 * <p>Each client registry represents a separate entity in the framework and
 * will have its own inbox created to act as the reply-to channel for all
 * requests sent from service proxies created by this registry. It is
 * therefore recommended that instances of this registry be limited and shared
 * where ever possible as each registry and its associated inbox spawns at
 * least one dispatch thread depending on the underlying transport.</p>
 *
 * <p>This registry also shares a single message transport for all messaging
 * operations. It should be considered the main client entity in the framework
 * and treated as such. If component desire to share a connection, they can
 * share this instance of the ClientRegistry.</p>
 */
public class ClientRegistry {
  /**
   * The Map of classes we search for implementations. The object is used for reflection.
   */
  private static final Map<Class,Object> proxyClasses;

  /**
   * Cache of configured instances to be reused
   */
  private static final Map<Class, Object> proxyCache = new HashMap<>();

  static {
    proxyClasses = ProxyListScanner.scan();
  }

  /**
   * Our transport builder, creates transports for proxy instances.
   */
  private final TransportBuilder transportBuilder = new TransportBuilder();
  /**
   * The transport shared by all the proxies.
   */
  private Transport transport = null;

  public ClientRegistry() {
    // new instances of this class mean new inbox an background thread to handle replies
  }


  /**
   * Opens a connection to a message transport, creates an inbox queue for
   * replies and OAM, and starts a dispatch thread running to handle all inbox
   * messages.
   *
   * @throws IOException if there were problems connection to the message transport.
   */
  public void open() throws IOException {

  }

  /**
   * This removes the inbox queue, terminates dispatch threads, clears out the
   * service proxy cache, closes the connection with the message transport,
   * and returns the registry to a new state ready to make another connection.
   *
   * <p>If a connection to a message transport fails, this method can be called
   * to clear out the registry in preparation for making a new connection.</p>
   */
  public void close() {

  }

  /**
   * Build a proxy of the given type connected to the messaging transport currently configured/
   *
   * @param type
   * @param <E>
   * @return the first type which implements the given interface type
   */
  public <E> E build(Class<E> type) {
    Object retval = null;

    // scan all the classes for one which implements the given interface


    try {
      //Class<?> clazz = Class.forName(className);
      Constructor<?> ctor = type.getConstructor();
      Object object = ctor.newInstance();

      if (object instanceof KestrelProxy) {
        retval = object;
//        try {
//          retval.setCommandLineArguments(args);
//          retval.configure(configuration);
//        } catch (ConfigurationException e) {
//          System.err.println(LogMsg.createMsg(MSG, "Loader.could_not_config_loader", object.getClass().getName(), e.getClass().getSimpleName(), e.getMessage()));
//        }
      } else {
        System.err.println("Not a Kestrel proxy");
      }
    } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      System.err.println(e.getLocalizedMessage());
    }

    return type.cast(retval);
  }


  /**
   * Add a class the builder should use to scan for proxy instances.
   *
   * <p>Instances of these classes will be created and configured to use this
   * registry's inbox for all reply-to headers.</p>
   *
   * @param proxyClass the class to be instantiated
   */
  public void addServiceProxyClass(Class proxyClass) {
    proxyClasses.put(proxyClass,null);
  }

  public ClientRegistry setURI(String uri) throws IllegalArgumentException {
    transportBuilder.setURI(uri);
    return this;
  }

  public ClientRegistry setURI(URI uri) throws IllegalArgumentException {
    transportBuilder.setURI(uri);
    return this;
  }

  public String getQuery() {
    return transportBuilder.getQuery();
  }

  public ClientRegistry setQuery(String path) {
    transportBuilder.setQuery(path);
    return this;
  }

  public int getPort() {
    return transportBuilder.getPort();
  }

  public ClientRegistry setPort(int port) {
    transportBuilder.setPort(port);
    return this;
  }

  public String getHostname() {
    return transportBuilder.getHostname();
  }

  public String getPassword() {
    return transportBuilder.getPassword();
  }

  public ClientRegistry setPassword(String password) {
    transportBuilder.setPassword(password);
    return this;
  }

  public String getUsername() {
    return transportBuilder.getUsername();
  }

  public ClientRegistry setUsername(String username) {
    transportBuilder.setUsername(username);
    return this;
  }

  public String getScheme() {
    return transportBuilder.getScheme();
  }

  public ClientRegistry setScheme(String scheme) {
    transportBuilder.setScheme(scheme);
    return this;
  }

  public int getConnectionTimeout() {
    return transportBuilder.getConnectionTimeout();
  }

  public ClientRegistry setConnectionTimeout(int timeout) {
    transportBuilder.setConnectionTimeout(timeout);
    return this;
  }

  public ClientRegistry setHost(String host) {
    transportBuilder.setHost(host);
    return this;
  }


}
