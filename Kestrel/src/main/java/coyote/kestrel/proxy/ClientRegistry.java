package coyote.kestrel.proxy;

import coyote.kestrel.transport.InvalidTransport;
import coyote.kestrel.transport.Transport;
import coyote.kestrel.transport.TransportBuilder;
import coyote.loader.cfg.Config;
import coyote.loader.log.Log;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.HashMap;
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
  private static final Map<Class, Config> proxyClasses;


  /**
   * Cache of configured instances to be used in this runtime
   */
  private static final Map<Class, Object> proxyCache;

  static {
    proxyClasses = ProxyListScanner.scan();
    proxyCache = populateCache(proxyClasses);
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

  private static Map<Class, Object> populateCache(Map<Class, Config> classMap) {
    Map<Class, Object> retval = new HashMap<>();
    for (Map.Entry entry : classMap.entrySet()) {
      Class type = (Class) entry.getKey();
      try {
        Constructor<?> ctor = type.getConstructor();
        Object object = ctor.newInstance();

        if (object instanceof KestrelProxy) {
          KestrelProxy proxy = (KestrelProxy) object;
          try {
            proxy.configure((Config) entry.getValue());
          } catch (Exception e) {
            Log.warn("Could not configure proxy '" + type.getCanonicalName() + "' Reason: " + e.getLocalizedMessage());
          }
        } else {
          System.err.println("Not a Kestrel proxy");
        }
        retval.put(type, object);
      } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
        System.err.println(e.getLocalizedMessage());
      }
    }
    return retval;
  }


  /**
   * Opens a connection to a message transport, creates an inbox queue for
   * replies and OAM, and starts a dispatch thread running to handle all inbox
   * messages.
   *
   * @throws IOException if there were problems connection to the message transport.
   */
  public void connect() throws IOException, IllegalStateException {
    if (transport == null) {
      transport = transportBuilder.build();
      if (transport instanceof InvalidTransport) {
        Log.fatal("Could not create a valid transport from set configuration.");
      } else {
        transport.open();
      }
    } else {
      if (transport.isValid()) {
        throw new IllegalStateException("Transport already connected");
      } else {
        transport.close();
        transport = transportBuilder.build();
      }
    }
  }

  /**
   * This removes the inbox queue, terminates dispatch threads, clears out the
   * service proxy cache, closes the connection with the message transport,
   * and returns the registry to a new state ready to make another connection.
   *
   * <p>If a connection to a message transport fails, this method can be called
   * to clear out the registry in preparation for making a new connection.</p>
   */
  public void disconnect() {

  }

  /**
   * Build a proxy of the given type connected to the messaging transport
   * currently configured.
   *
   * <p>SIDE EFFECT: This will open the registry if it is not already opened.
   * This is by design for ease of use.</p>
   *
   * @param type the service interface to locate
   * @param <E>  a configured service proxy which implements that service interface.
   * @return the first type which implements the given interface type
   */
  public <E> E locate(Class<E> type) {
    Object retval = locateObject(type);

    // make sure we have a transport to set in the proxy
    if (transport == null) {
      try {
        connect();
      } catch (IOException e) {
        Log.error("Could not connect");
      }
    }

    try {
      if (retval instanceof KestrelProxy) {
        KestrelProxy proxy = (KestrelProxy) retval;
        // make sure the proxy has a transport
        if (proxy.getTransport() == null) {
          proxy.setTransport(transport);
        }
        // make sure the proxy is initialized
        if( proxy.isInitialized()){
          proxy.initialize();
        }
      }
    } catch (SecurityException | IllegalArgumentException e) {
      Log.error("Could not set transport on proxy: " + e.getLocalizedMessage());
    }

    // return the proxy which implements the given type or null if not found
    return type.cast(retval);
  }


  private <E> Object locateObject(Class<E> type) {
    Object retval = null;
    for (Map.Entry entry : proxyCache.entrySet()) {
      Class clazz = (Class) entry.getKey();
      Object proxy = entry.getValue();
      if (type.isInstance(proxy)) {
        retval = proxy;
        break;
      }
    }
    return retval;
  }


  /**
   * Add a class the builder should use to scan for proxy instances.
   *
   * <p>Instances of these classes will be created and configured to use this
   * registry's inbox for all reply-to headers.</p>
   *
   * @param proxyClass the class to be instantiated
   */
  public ClientRegistry addServiceProxyClass(Class proxyClass) {
    return addServiceProxyClass(proxyClass, null);
  }

  /**
   * Add a class the builder should use to scan for proxy instances.
   *
   * <p>Instances of these classes will be created and configured to use this
   * registry's inbox for all reply-to headers.</p>
   *
   * @param proxyClass the class to be instantiated
   * @param cfg        the configuration to use for the proxy (if applicable)
   */
  public ClientRegistry addServiceProxyClass(Class proxyClass, Config cfg) {
    if (proxyClass != null) {
      try {
        Constructor<?> ctor = proxyClass.getConstructor();
        Object object = ctor.newInstance();
        if (cfg != null) {
          if (object instanceof KestrelProxy) {
            KestrelProxy proxy = (KestrelProxy) object;
            try {
              proxy.configure(cfg);
            } catch (Exception e) {
              Log.warn("Could not configure proxy '" + proxyClass.getCanonicalName() + "' Reason: " + e.getLocalizedMessage());
            }
          } else {
            System.err.println("Not a Kestrel proxy");
          }
        }
        proxyCache.put(proxyClass, object);
      } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
        System.err.println(e.getLocalizedMessage());
      }
      proxyClasses.put(proxyClass, cfg);
    }
    return this;
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
