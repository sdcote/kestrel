package coyote.kestrel.protocol;

import coyote.commons.StringUtil;
import coyote.i13n.Timer;
import coyote.kestrel.transport.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * A Future object representing a collection point for responses to requests.
 */
public class ResponseFuture {
  private String identifier = null;
  private Message request;
  private List<Message> responses = new ArrayList<>();
  private long started;
  private long expiry = Long.MAX_VALUE; // epoch time im milliseconds when the future expires
  private Timer timer = null;

  /**
   * Create a response future with the given request message and no timeout.
   *
   * @param request the request message this response will correlate.
   */
  public ResponseFuture(Message request) {
    started = System.currentTimeMillis();
    if (request != null) {
      this.request = request;
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
   * Set how long the response future is considered active.
   *
   * <p>Active response futures will remain open for receiving response
   * messages. Inactive response futures should be considered stale and
   * removed from caches.</p>
   *
   * @param timestamp the time in the future when the response future expires
   */
  public ResponseFuture setExpiry(long timestamp) {
    expiry = timestamp;
    return this;
  }

  public String getIdentifier() {
    return identifier;
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
    if (timer != null) timer.stop();
    return this;
  }

  /**
   * @return true if there are no response and the response time out has not expired.
   */
  public boolean isWaiting() {
    return (responses.size() == 0 && System.currentTimeMillis() < expiry);
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

  public void setTimer(Timer timer) {
    this.timer = timer;
  }


  /**
   * Add the given number of milliseconds to the started time
   * @param age
   */
  public void setTimeout(int age) {
    if (age > 0)
      setExpiry(started + age);
    else
      setExpiry(started);
  }

}
