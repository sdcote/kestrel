package coyote.profile;


import coyote.kestrel.AbstractService;
import coyote.kestrel.KestrelService;
import coyote.kestrel.transport.Message;

public class ProfileService extends AbstractService implements KestrelService {


  private static final String GROUP_NAME = "SVC.PROFILE";


  @Override
  public String getGroupName() {
    return GROUP_NAME;
  }


  /**
   * This is where we receive our request messages.
   *
   * @param message
   */
  @Override
  public void process(Message message) {

    Message response = message.createResponse();
    // fill it with stuff

    respond(response);

  }

}
