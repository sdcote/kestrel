package coyote.kestrel.protocol;

import coyote.kestrel.transport.Message;
import coyote.kestrel.transport.MessageQueue;
import coyote.kestrel.transport.Transport;

/**
 * Represents a base class for message groups to provide uniformity to message exchanges.
 *
 * <p>The Kestrel service protocol is implemented here.</p>
 */
public abstract class KestrelServiceGroup implements MessageGroup {

  protected String groupName;
  protected Transport transport = null;
  MessageQueue messageQueue = null;

  @Override
  public Message getNextMessage() {
    Message retval = null;
    if (messageQueue != null) {
      retval = messageQueue.getNextMessage();
      if (retval != null) {
        retval.setGroup(groupName);
      }
    }
    return retval;
  }


  @Override
  public void respond(Message response) {

  }

  @Override
  public ResponseFuture request(Message request) {
    return null;
  }

  @Override
  public void send(Message event) {

  }

  @Override
  public void setTransport(Transport transport) {
    this.transport = transport;
  }

  @Override
  public void initialize() {
    if (transport != null) {

      // get a shared queue with the name of this group
      messageQueue = transport.getQueue(getGroupName());

    } else {
      throw new IllegalStateException("Service group has no transport set.");
    }

  }

  protected String getGroupName() {
    return groupName;
  }

  protected void setGroupName(String name) {
    groupName = name;
  }


}
