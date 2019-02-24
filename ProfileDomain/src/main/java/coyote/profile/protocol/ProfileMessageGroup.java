package coyote.profile.protocol;

public class ProfileMessageGroup extends KestrelMessageGroup implements MessageGroup {

  private static final String PROFILE_GROUP_NAME = "SVC.PROFILE";

  public ProfileMessageGroup(){
    GROUP_NAME = PROFILE_GROUP_NAME;
  }

}
