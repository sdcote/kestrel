package coyote.kestrel.proxy;

import coyote.kestrel.transport.Message;
import coyote.kestrel.transport.MessageListener;
import coyote.kestrel.transport.MessageQueue;
import coyote.kestrel.transport.Transport;
import coyote.loader.log.Log;

/**
 * The base class for all service proxies
 */
public class AbstractProxy implements KestrelProxy, MessageListener {
  protected MessageQueue inbox = null;
  protected Transport transport = null;


  public AbstractProxy() {
    Log.info("proxy initializing");
    initializeInbox();
  }



  private Transport getTransport() {
    return transport;
  }

  @Override
  public void setTransport(Transport transport) {
    this.transport = transport;
  }

  private void initializeInbox() {
    try {
      // create an inbox on which we will receive message directly to us
      inbox = getTransport().createInbox();
      // start receiving messages and send them to this listener
      inbox.attach(this);
    } catch (Exception e) {
      Log.error("Could not initialize the inbox");
    }
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


  }

}
