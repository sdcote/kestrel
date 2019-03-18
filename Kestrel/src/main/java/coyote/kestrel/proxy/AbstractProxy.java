package coyote.kestrel.proxy;

import com.rabbitmq.client.Channel;
import coyote.dataframe.marshal.JSONMarshaler;
import coyote.i13n.StatBoard;
import coyote.i13n.StatBoardImpl;
import coyote.kestrel.protocol.ResponseFuture;
import coyote.kestrel.transport.Message;
import coyote.kestrel.transport.MessageListener;
import coyote.kestrel.transport.MessageQueue;
import coyote.kestrel.transport.Transport;
import coyote.loader.cfg.Config;
import coyote.loader.cfg.ConfigurationException;
import coyote.loader.log.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * The base class for all service proxies
 */
public abstract class AbstractProxy implements KestrelProxy, MessageListener {
  /**
   * The component responsible for tracking operational statistics
   */
  private static final StatBoard stats = new StatBoardImpl();
  protected static MessageQueue inbox = null;
  protected static Transport transport = null;
  protected static Channel channel = null;
  protected Config configuration = null;
  private boolean initializedFlag = false;
  private Map<String, ResponseFuture> responseCache = new HashMap<>();

  public AbstractProxy() {
    Log.info("proxy initializing");
  }


  @Override
  public Transport getTransport() {
    return transport;
  }

  @Override
  public void setTransport(Transport transport) {
    AbstractProxy.transport = transport;
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


  /**
   * Create a message suitable for publishing on the given group.
   *
   * @param messageGroup The name of the message group on which the message is to be sent.
   * @return a message with the group and identifier set.
   */
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
    if (!recordResponse(message)) {
      processMessage(message);
    }
    cleanCache();
  }

  /**
   * Remove all expired response futures from the cache.
   */
  protected void cleanCache() {
    synchronized (responseCache) {
      for (Iterator<Map.Entry<String, ResponseFuture>> it = responseCache.entrySet().iterator(); it.hasNext(); ) {
        Map.Entry<String, ResponseFuture> entry = it.next();
        if (entry.getValue().isExpired()) {
          it.remove();
        }
      }
    }
  }


  @Override
  public void processMessage(Message message) {
    Log.debug("Proxy received message that did not correlate to an active request : " + message.getId());
  }

  /**
   * Place the message into the correlated response future.
   *
   * @param message the message to place in the response
   * @return true if the message was placed, false otherwise.
   */
  private boolean recordResponse(Message message) {
    boolean retval = false;
    ResponseFuture future = getResponse(message);
    if (future != null) {
      future.addResponse(message);
      retval = true;
    }
    return retval;
  }

  private ResponseFuture getResponse(Message message) {
    return responseCache.get(message.getReplyId());
  }


  /**
   * Send the message and return the response future object to track responses.
   *
   * <p>The caller should take care and remove the response object from the
   * response queue by calling clearCache which will remove all the expired
   * response futures from the cache.</p>
   *
   * @param message the (request) message to send
   * @return the response future object where responses will be recorded.
   * @throws IOException
   */
  protected ResponseFuture send(Message message) throws IOException {
    ResponseFuture retval = new ResponseFuture(message);
    responseCache.put(retval.getIdentifier(), retval);
    if (!isInitialized()) {
      initialize();
    }
    message.setReplyGroup(inbox.getName());
    getTransport().send(message);
    return retval;
  }

  /**
   * Send the request and wait up to the time-out interval for responses.
   *
   * <p>This method will remove the response future from the response cache
   * before returning to help keep the response cache clean. This means only
   * one response will be correlated for this request. If multiple responses
   * are expected, the @code{send(Message} method should be used with the
   * caller managing timeouts and response counts.</p>
   *
   * @param request The request message to send
   * @param timeout How long to wait (in milliseconds) for responses.
   * @return The response future object with at least one response or after the timeout has expired.
   */
  protected ResponseFuture sendAndWait(Message request, int timeout) {
    ResponseFuture retval = null;
    try {
      retval = send(request);
      retval.setTimeout(timeout);
      while (retval.isWaiting()) {
        try {
          Thread.sleep(10);
        } catch (InterruptedException ignore) {
        }
      }
    } catch (IOException e) {
      Log.fatal("Could not send message: " + e.getLocalizedMessage());
    }

    // clear this future from the response cache
    if (retval != null) {
      responseCache.remove(retval.getIdentifier());
    }

    return retval;
  }

}
