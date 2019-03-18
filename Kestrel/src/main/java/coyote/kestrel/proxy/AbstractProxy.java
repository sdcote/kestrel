package coyote.kestrel.proxy;

import com.rabbitmq.client.Channel;
import coyote.i13n.StatBoard;
import coyote.i13n.StatBoardImpl;
import coyote.kestrel.transport.Message;
import coyote.kestrel.transport.MessageListener;
import coyote.kestrel.transport.MessageQueue;
import coyote.kestrel.transport.Transport;
import coyote.loader.cfg.Config;
import coyote.loader.cfg.ConfigurationException;
import coyote.loader.log.Log;

import java.io.IOException;

/**
 * The base class for all service proxies
 */
public abstract class AbstractProxy implements KestrelProxy, MessageListener {
  protected static MessageQueue inbox = null;
  protected static Transport transport = null;
  protected static Channel channel = null;
  protected Config configuration = null;
  /**
   * The component responsible for tracking operational statistics
   */
  private static final StatBoard stats = new StatBoardImpl();
  private boolean initializedFlag = false;

  public AbstractProxy() {
    Log.info("proxy initializing");
  }


  @Override
  public Transport getTransport() {
    return transport;
  }

  @Override
  public void setTransport(Transport transport) {
    this.transport = transport;
  }

  @Override
  public boolean isInitialized() {
    return initializedFlag;
  }

  @Override
  public void initialize() {
    if (transport != null) {
      try {
        inbox = getTransport().createInbox();
        inbox.attach(this);
        stats.setId(inbox.getName());
        initializedFlag = true;
        onInitialization();
      } catch (Exception e) {
        Log.error("Could not initialize service proxy inbox");
      }
    } else {
      Log.error("Cannot initialize service proxy: transport not set");
    }
  }

  @Override
  public StatBoard getStatBoard() {
    return stats;
  }


  @Override
  public void configure(Config cfg) throws ConfigurationException {
    configuration = cfg;
    onConfiguration();
  }


  protected Message createMessage(String messageGroup) {
    Message request = new Message();
    request.setGroup(messageGroup);
    request.generateId();
    return request;
  }


  /**
   * This method is called when the configuration is set to the subclass can
   * perform its own configuration processing
   */
  protected void onConfiguration() throws ConfigurationException {
    // no-op implementation
  }

  /**
   * This method is called when the initialization method is complete and
   * successful.
   */
  protected void onInitialization() {
    // no-op implementation
  }


  /**
   * This is where all our responses are received.
   *
   * <p>The primary goal of this method is pairing responses to request
   * futures.</p>
   *
   * <p>The secondary goal is to process OAM messages to control how this
   * client should operate.</p>
   *
   * @param message The message to receive and process.
   */
  @Override
  public void onMessage(Message message) {
    System.out.println("Proxy received: " + message);
  }



  protected void send(Message message) throws IOException {
    getTransport().send(message);
  }
}
