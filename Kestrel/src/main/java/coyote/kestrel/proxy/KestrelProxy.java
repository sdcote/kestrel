package coyote.kestrel.proxy;

import coyote.i13n.StatBoard;
import coyote.kestrel.transport.Message;
import coyote.kestrel.transport.Transport;
import coyote.loader.cfg.Config;
import coyote.loader.cfg.ConfigurationException;

/**
 * This represents the Kestrel related processing
 */
public interface KestrelProxy {

  void configure(Config cfg) throws ConfigurationException;

  Transport getTransport();

  void setTransport(Transport transport);

  boolean isInitialized();

  void initialize();

  StatBoard getStatBoard();

  void processMessage(Message message);

}
