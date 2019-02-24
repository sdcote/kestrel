package coyote.kestrel;

import coyote.kestrel.protocol.KestrelMessageGroup;
import coyote.kestrel.protocol.MessageGroup;
import coyote.kestrel.protocol.ResponseFuture;
import coyote.kestrel.transport.Message;


/**
 * The service message group allows all services to broadcast information
 * about themselves for discovery, monitoring and operations.
 */
public class ServiceMessageGroup extends KestrelMessageGroup implements MessageGroup {

  private static final String MESSAGE_GROUP_NAME = "SVC.REGISTRY";

  public ServiceMessageGroup(){
    GROUP_NAME = MESSAGE_GROUP_NAME;
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


}
