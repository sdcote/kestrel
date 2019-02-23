package coyote.kestrel;

import coyote.loader.cfg.ConfigurationException;

public interface CoyoteService {

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
   * @param packet
   */
  void process(Packet packet);




  /**
   * Called when the JVM terminates giving the service a chance to perform any
   * last minute processing.
   */
  void onShutdown();

}
