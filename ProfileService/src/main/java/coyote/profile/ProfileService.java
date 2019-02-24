package coyote.profile;


import coyote.kestrel.AbstractService;
import coyote.kestrel.KestrelService;
import coyote.kestrel.transport.Message;
import coyote.loader.log.LogMsg;
import coyote.profile.protocol.ProfileMessageGroup;

public class ProfileService extends AbstractService implements KestrelService {


  public static final LogMsg.BundleBaseName MSG;
  static {
    MSG = new LogMsg.BundleBaseName("ProfileMsg");
  }

  /** What we use to communicate with clients */
  ProfileMessageGroup group = new ProfileMessageGroup();




  /**
   * This is where we receive our request messages.
   *
   * @param message
   */
  @Override
  public void process(Message message){

    Message response = message.createResponse();
    // fill it with stuff

    //response.send();

  }



}
