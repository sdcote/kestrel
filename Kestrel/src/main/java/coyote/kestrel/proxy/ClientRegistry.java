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
  private static final Map<Class, Config> proxyClasses = new HashMap<>();

  /**
   * Cache of configured instances to be used in this runtime
   */
  private static final Map<Class, Object> proxyCache = new HashMap<>();

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
   * @throws IllegalStateException if there were problems connection to the message transport.
   */
  protected void connect() throws IllegalStateException {
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
    transport.close();
    transport = null;
  }


  /**
   * Build a proxy of the given type connected to the messaging transport
   * currently configured.
   *
   * <p>SIDE EFFECT: This will open the registry if it is not already opened.
   * This is by design for ease of use and ensuring the returned proxy
   * instances are ready to use.</p>
   *
   * @param type the service interface to locate
   * @param <E> the type of the class to locate
   * @return the first type which implements the given interface type
   */
  public <E> E locate(Class<E> type) {
    if (transport == null) {
      try {
        connect();
      } catch (Exception e) {
        Log.error("Could not connect");
      }
    }

    Object retval = findOrCreateProxy(type);

    // return the proxy which implements the given type or null if not found
    return type.cast(retval);
  }


  /**
   * Find an existing service proxy in the cache, or create a new instance if
   * it does not exist.
   *
   * <p>This assumes a proxy class has been registered with the requested
   * interface. If not, then null will be returned.</p>
   *
   * <p>The returned object will be configured, if it is an instance of
   * KestrelProxy and a Config object has been registered with the proxy
   * class.</p>
   *
   * <p>If the object was created, the transport will be set in the new proxy
   * instance and it will be initialized.</p>
   *
   * @param type the type (i.e. interface) for which to search the registered classes.
   * @return an object implementing the requested type, or null if none of the registered classes implement the given type.
   */
  private <E> Object findOrCreateProxy(Class<E> type) {
    Object retval = null;
    for (Map.Entry<Class, Object> entry : proxyCache.entrySet()) {
      if (type.isInstance(entry.getValue())) {
        retval = entry.getValue();
        break;
      }
    }

    if (retval == null) {
      for (Map.Entry<Class, Config> entry : proxyClasses.entrySet()) {
        try {
          Constructor<?> ctor = entry.getKey().getConstructor();
          Object proxy = ctor.newInstance();
          if (type.isInstance(proxy)) {
            retval = proxy;
            proxyCache.put(type, proxy);
            if (proxy instanceof KestrelProxy) {
              KestrelProxy kestrelProxy = (KestrelProxy) proxy;
              try {
                kestrelProxy.configure(entry.getValue());
                kestrelProxy.setTransport(transport);
                try {
                  kestrelProxy.initialize();
                } catch (Exception e) {
                  Log.error("Problems initializing proxy: " + proxy.getClass().getCanonicalName() + " - Reason: " + e.getLocalizedMessage());
                }
              } catch (Exception e) {
                Log.warn("Could not configure proxy '" + proxy.getClass().getCanonicalName() + "' Reason: " + e.getLocalizedMessage());
              }
            } else {
              Log.notice("Proxy '" + proxy.getClass().getCanonicalName() + " is not a Kestrel proxy, no configuration or initialization performed");
            }
            break;
          }
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
          e.printStackTrace();
        }
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
   * @return this registry for command chaining
   */
  public ClientRegistry addServiceProxyClass(Class proxyClass) {
    return addServiceProxyClass(proxyClass, null);
  }


  /**
   * Add a class the registry should use to scan for proxy instances.
   *
   * <p>Proxy classes can only be added once, if another request is received
   * to add the same proxy class, it is ignored. THis is by design to help
   * ensure proxy instances are not orphaned and are shared between
   * components.</p>
   *
   * @param proxyClass the class to be instantiated
   * @param cfg        the configuration to use for the proxy (if applicable)
   * @return this registry for command chaining
   */
  public ClientRegistry addServiceProxyClass(Class proxyClass, Config cfg) {
    if (proxyClass != null) {
      if (!proxyClasses.containsKey(proxyClass)) {
        proxyClasses.put(proxyClass, cfg);
      }
    } else {
      throw new IllegalArgumentException("Cannot add a null service proxy class to registry");
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
