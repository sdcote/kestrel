package coyote.profile.protocol;

import coyote.kestrel.protocol.KestrelMessageGroup;
import coyote.kestrel.protocol.MessageGroup;
import coyote.kestrel.protocol.ResponseFuture;
import coyote.kestrel.transport.Message;

public class ProfileMessageGroup extends KestrelMessageGroup implements MessageGroup {

  private static final String MESSAGE_GROUP_NAME = "SVC.PROFILE";

  public ProfileMessageGroup(){
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
