package coyote.kestrel.protocol;

import coyote.commons.StringUtil;
import coyote.kestrel.transport.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * A Future object representing a collection point for responses to requests.
 */
public class ResponseFuture {
  private static final int DEFAULT_ACTIVE_TIMEOUT = 15000;
  private String identifier = null;
  private Message request;
  private List<Message> responses = new ArrayList<>();
  private long started = System.currentTimeMillis();
  private long expiry = Long.MAX_VALUE;
  private int timeout = DEFAULT_ACTIVE_TIMEOUT;

  /**
   * Create a response future with the given request message and no timeout.
   *
   * @param request the request message this response will correlate.
   */
  public ResponseFuture(Message request) {
    if (request != null) {
      if (StringUtil.isNotBlank(request.getId())) {
        identifier = request.getId();
      } else {
        identifier = request.generateId();
      }
    }
  }

  /**
   * Create a response future with the given request message and timeout.
   *
   * @param request the request message this response will correlate.
   * @param timeout the number of milliseconds in the future when this response is considered expired.
   */
  public ResponseFuture(Message request, int timeout) {
    this(request);
    setTimeout(timeout);
  }

  /**
   * @return the number of milliseconds the response future is considered active.
   */
  public int getTimeout() {
    return timeout;
  }

  /**
   * Set how long the response future is considered active.
   *
   * <p>Active response futures will remain open for receiving response
   * messages. Inactive response futures should be considered stale and
   * removed from caches.</p>
   *
   * @param millis the number of milliseconds the response future remains active.
   */
  public ResponseFuture setTimeout(int millis) {
    if (millis > 0)
      expiry = started + millis;
    else
      expiry = started;
    return this;
  }

  public String getIdentifier() {
    return identifier;
  }

  public ResponseFuture setIdentifier(String identifier) {
    this.identifier = identifier;
    return this;
  }

  public boolean hasResponses() {
    return responses.size() > 0;
  }

  public boolean isExpired() {
    return System.currentTimeMillis() > expiry;
  }

  public ResponseFuture addResponse(Message message) {
    synchronized (responses) {
      responses.add(message);
    }
    return this;
  }

  /**
   * @return true if there are no response and the response time out has not expired.
   */
  public boolean isWaiting() {
    return (responses.size() == 0 && System.currentTimeMillis() > expiry);
  }


  /**
   * @return a mutable list of responses currently recorded in this response future.
   */
  public List<Message> getResponses() {
    List<Message> retval = new ArrayList<>();
    synchronized (responses) {
      for (Message msg : responses) {
        retval.add(msg);
      }
    }
    return retval;
  }

}
