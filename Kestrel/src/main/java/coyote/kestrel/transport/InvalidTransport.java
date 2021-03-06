package coyote.kestrel.transport;

import java.io.IOException;

/**
 * Instance of a transport which is the result of not being able to connect to
 * the broker.
 *
 * <p>This type is designed to reduce the number of null pointer exceptions
 * and it will be returned when a variety of operations fail, not just
 * connections.</p>
 */
public class InvalidTransport implements Transport {

  private static final String MESSAGE = "Could not connect to transport";

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

  @Override
  public void sendDirect(Message msg) throws IOException {
    throw new IOException("Invalid transport; not connected");
  }

  @Override
  public void broadcast(Message msg) throws IOException {
    throw new IOException("Invalid transport; not connected");
  }

}
