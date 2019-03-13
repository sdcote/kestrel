package coyote.kestrel.transport;

public interface MessageQueue extends MessageChannel {

  Message getNextMessage();

  Message peek();
  Message peek(long timeout);
}
