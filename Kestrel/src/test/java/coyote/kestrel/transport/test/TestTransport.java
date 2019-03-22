package coyote.kestrel.transport.test;

import coyote.kestrel.transport.Message;
import coyote.kestrel.transport.MessageQueue;
import coyote.kestrel.transport.MessageTopic;
import coyote.kestrel.transport.Transport;

import java.io.IOException;

public class TestTransport implements Transport {

  @Override
  public boolean isValid() {
    return true;
  }

  @Override
  public MessageQueue createInbox() {
    return new TestQueue();
  }

  @Override
  public void open() {

  }

  @Override
  public void close() {

  }

  @Override
  public MessageQueue getServiceQueue(String name) {
    return new TestQueue();
  }

  @Override
  public MessageTopic getTopic(String name) {
    return new TestTopic();
  }

  @Override
  public void sendDirect(Message message) throws IOException {

  }

  @Override
  public void broadcast(Message message) throws IOException {

  }
}
