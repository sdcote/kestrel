package coyote.kestrel.protocol;

import coyote.kestrel.transport.Message;
import coyote.kestrel.transport.Transport;

/**
 * Represents a base class for message groups to provide uniformity to message exchanges.
 */
public abstract class KestrelMessageGroup implements MessageGroup {

  protected String GROUP_NAME;

  @Override
  public Message getNextMessage() {
    return null;
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

  }

  @Override
  public void setGroup(String groupName) {

  }

  @Override
  public void initialize() {

  }


}
