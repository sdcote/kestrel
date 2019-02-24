package coyote.profile.protocol;

import coyote.kestrel.protocol.KestrelMessageGroup;
import coyote.kestrel.protocol.MessageGroup;

public class ProfileMessageGroup extends KestrelMessageGroup implements MessageGroup {

  private static final String PROFILE_GROUP_NAME = "SVC.PROFILE";

  public ProfileMessageGroup(){
    GROUP_NAME = PROFILE_GROUP_NAME;
  }

}
