package coyote.kestrel.transport;

/**
 * Instance of a transport which is the result of not being able to connect to the broker.
 */
public class InvalidTransport implements Transport {

  private final String MESSAGE = "Could not connect to transport";

  @Override
  public boolean isValid() {
    return false;
  }

  @Override
  public MessageQueue createInbox() {
    throw new IllegalStateException(MESSAGE);
  }

  @Override
  public void open() {
    throw new IllegalStateException(MESSAGE);
  }

  @Override
  public void close() {
    throw new IllegalStateException(MESSAGE);
  }

  @Override
  public MessageQueue getServiceQueue(String name) {
    throw new IllegalStateException(MESSAGE);
  }

  @Override
  public MessageTopic getTopic(String name) {
    throw new IllegalStateException(MESSAGE);
  }
}
