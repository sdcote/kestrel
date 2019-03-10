package coyote.kestrel.transport.amqp;

import coyote.kestrel.transport.Inbox;
import coyote.kestrel.transport.Message;

import java.util.UUID;

public class AmqpInbox implements Inbox {

  private final String identifier = UUID.randomUUID().toString();
  private AmqpQueue queue = null;


  public String getIdentifier() {
    return identifier;
  }

  @Override
  public Message getNextMessage() {
    Message retval = null;
    if (queue != null) {
      retval = queue.getNextMessage();
    }
    return retval;
  }


  public void setQueue(AmqpQueue queue) {
    this.queue = queue;
  }
}
