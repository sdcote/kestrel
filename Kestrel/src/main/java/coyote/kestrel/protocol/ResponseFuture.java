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
  private volatile boolean open;

  /**
   * Create a response future with the given request message and no timeout.
   *
   * @param request the request message this response will correlate.
   */
  public ResponseFuture(Message request) {
    started = System.currentTimeMillis();
    open = true;
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
   * @return this future for chaining.
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

  /**
   * Add the response message to the list of responses.
   *
   * <p>If the future is closed, the message is silently ignored.</p>
   *
   * @param message the message to add
   * @return this future for chaining.
   */
  public ResponseFuture addResponse(Message message) {
    if (open) {
      synchronized (responses) {
        responses.add(message);
      }
      if (timer != null) timer.stop();
    }
    return this;
  }

  /**
   * Indicates if the future is accepting response messages and the timer is running.
   *
   * @return true if open false if closed.
   */
  public boolean isOpen() {
    return open;
  }

  /**
   * @return true if there are no response, is open, and the response time out has not expired.
   */
  public boolean isWaiting() {
    return (responses.size() == 0 && open && System.currentTimeMillis() < expiry);
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
   *
   * @param age number of milliseconds to add
   */
  public void setTimeout(int age) {
    if (age > 0)
      setExpiry(started + age);
    else
      setExpiry(started);
  }

  /**
   * Stop the future from collecting responses, and stop the timer.
   */
  public void close() {
    open = false;
    if (timer != null) timer.stop();
  }
}
