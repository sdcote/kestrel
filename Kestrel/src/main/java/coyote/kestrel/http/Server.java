package coyote.kestrel.http;

import coyote.commons.StringUtil;
import coyote.commons.WebServer;
import coyote.kestrel.proxy.ClientRegistry;
import coyote.kestrel.service.KestrelService;
import coyote.loader.cfg.Config;
import coyote.loader.cfg.ConfigurationException;

public class Server extends WebServer {
  private ClientRegistry registry = null;

  /**
   * @see coyote.loader.AbstractLoader#configure(coyote.loader.cfg.Config)
   */
  @Override
  public void configure(Config config) throws ConfigurationException {
    super.configure(config);

    registry = new ClientRegistry();

    Config cfg = configuration.getSection(KestrelService.TRANSPORT_SECTION);

    if (cfg == null) {
      throw new ConfigurationException("No transport configuration section found");
    }

    String uri = cfg.getString(KestrelService.URI_TAG);

    if (StringUtil.isBlank(uri)) {
      throw new ConfigurationException("Could not retrieve transport URI from configuration");
    }

    registry.setURI(uri);

  }


  /**
   * Allow responders to find proxies to their services.
   *
   * @param type the service interface class to locate
   * @return an instance of a proxy implementing that class or null if none were found.
   */
  public <E> E locateProxy(Class<E> type) {
    return registry.locate(type);
  }


  public ClientRegistry addServiceProxyClass(Class proxyClass, Config cfg) {
    return registry.addServiceProxyClass(proxyClass,cfg);
  }


}
