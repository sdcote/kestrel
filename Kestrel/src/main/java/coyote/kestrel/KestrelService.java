package coyote.kestrel;

import coyote.kestrel.transport.Message;
import coyote.loader.cfg.ConfigurationException;

public interface KestrelService {

  /**
   * Called after the base class has been configured with the expectation the
   * subclass will perform any configuration and initialization processing.
   *
   * @throws ConfigurationException if the service could not be configured.
   */
  void onConfiguration() throws ConfigurationException;




  /**
   * Process received packets.
   *
   * <p>This is where the service is implemented.</p>
   *
   * @param packet the data received from the transport.
   */
  void process(Message packet);




  /**
   * Called when the JVM terminates giving the service a chance to perform any
   * last minute processing.
   */
  void onShutdown();

}
