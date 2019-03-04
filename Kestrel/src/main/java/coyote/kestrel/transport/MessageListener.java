package coyote.kestrel.transport;

/**
 * Object that implement this interface are enabled to be used as a call-back
 * for MessageTransports
 */
public interface MessageListener {

  /**
   * Allow messages to be sent to the object implementing this interface.
   *
   * <p>This is a call-back method that usually results when something places a
   * message in a Message Transport with which we are registered.</p>
   *
   * @param message The message to receive and process.
   * @throws IllegalStateException if the system is in the runtime of shutting-down
   */
  public void onMessage(Message message);

}
