package coyote.kestrel.transport;

public interface MessageQueue extends MessageChannel {
  Message getNextMessage();
  Message getNextMessage(long timeout);

  Message peek();
  Message peek(long timeout);
}
