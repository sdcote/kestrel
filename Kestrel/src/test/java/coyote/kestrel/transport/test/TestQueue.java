package coyote.kestrel.transport.test;

import coyote.kestrel.transport.Message;
import coyote.kestrel.transport.MessageListener;
import coyote.kestrel.transport.MessageQueue;

import java.io.IOException;

public class TestQueue implements MessageQueue {
  @Override
  public Message getNextMessage() {
    return null;
  }

  @Override
  public Message peek() {
    return null;
  }

  @Override
  public Message peek(long timeout) {
    return null;
  }

  @Override
  public String getName() {
    return null;
  }

  @Override
  public void setName(String name) {

  }

  @Override
  public void attach(MessageListener listener) {

  }

  @Override
  public void detach(MessageListener listener) {

  }

  @Override
  public void ackDelivery(Message message) {

  }

  @Override
  public void nakDelivery(Message message) {

  }

  @Override
  public void send(Message message) throws IOException {

  }
}
