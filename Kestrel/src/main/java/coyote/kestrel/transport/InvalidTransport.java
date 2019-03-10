package coyote.kestrel.transport;

/**
 * Instance of a transport which is the result of not being able to connect to the broker.
 */
public class InvalidTransport implements Transport {

  private final String MESSAGE = "Could not connect to broker";

  @Override
  public boolean isValid() {
    return false;
  }

  @Override
  public Inbox createInboxChannel() {
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
  public MessageQueue getQueue(String name) {
    throw new IllegalStateException(MESSAGE);
  }

  @Override
  public MessageTopic getTopic(String name) {
    throw new IllegalStateException(MESSAGE);
  }
}