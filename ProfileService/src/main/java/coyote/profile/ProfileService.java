package coyote.profile;


import coyote.dataframe.DataFrame;
import coyote.kestrel.AbstractService;
import coyote.kestrel.transport.Message;

public class ProfileService extends AbstractService {


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

    if( message.contains("ID")) {
      Message response = message.createResponse();

      DataFrame payload = new DataFrame();
      // fill it with stuff
      response.setPayload(payload);
      respond(response);
    } else {
      sendNak(message,"No id found");
    }

  }



}
