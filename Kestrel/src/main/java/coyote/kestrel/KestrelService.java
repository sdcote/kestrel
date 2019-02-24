package coyote.kestrel;

import coyote.kestrel.transport.Message;
import coyote.loader.cfg.ConfigurationException;

public interface KestrelService {

  /**
   * Name of the transport configuration section
   */
  String TRANSPORT_SECTION = "Transport";

  /**
   * Name of the URI configuration element
   */
  String URI_TAG = "URI";


  /**
   * Called after the base class has been configured with the expectation the
   * subclass will perform any configuration and initialization processing.
   *
   * @throws ConfigurationException if the service could not be configured.
   */
  void onConfiguration() throws ConfigurationException;


  /**
   * Process received messages.
   *
   * <p>This is where the service is implemented.</p>
   *
   * @param message the data received from the transport.
   */
  void process(Message message);


  /**
   * Process messages received from our Inbox.
   *
   * <p>Inbox messages are normally commands for the service such as cycle
   * logs, refresh caches or terminate. The service should make every effort
   * to validate the commands before processing them.</p>
   *
   * @param message the data received from the transport.
   */
  void processInboxMessage(Message message);


  /**
   * Called when the JVM terminates giving the service a chance to perform any
   * last minute processing.
   */
  void onShutdown();

}
