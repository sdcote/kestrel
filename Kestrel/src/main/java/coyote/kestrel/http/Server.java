package coyote.kestrel.http;

import coyote.commons.StringUtil;
import coyote.commons.WebServer;
import coyote.kestrel.proxy.ClientRegistry;
import coyote.kestrel.service.KestrelService;
import coyote.loader.cfg.Config;
import coyote.loader.cfg.ConfigurationException;

public class Server extends WebServer {
  private ClientRegistry registry = new ClientRegistry();

  /**
   * @see coyote.loader.AbstractLoader#configure(coyote.loader.cfg.Config)
   */
  @Override
  public void configure(Config config) throws ConfigurationException {
    super.configure(config);

    // look for the transport section in the configuration
    Config cfg = configuration.getSection(KestrelService.TRANSPORT_SECTION);
    if (cfg == null) throw new ConfigurationException("No transport configuration section found");
    String uri = cfg.getString(KestrelService.URI_TAG);
    if (StringUtil.isBlank(uri))
      throw new ConfigurationException("Could not retrieve transport URI from configuration");
    registry.setURI(uri);

  }

  public <E> E locate(Class<E> type) {
    return registry.locate(type);
  }

}
