package coyote.profile;


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
    if (message.contains("ID")) {
      Message response = message.createResponse();
      response.getPayload().clear();
      response.getPayload().put("ResponseCode", 203);
      send(response);
    } else {
      sendNak(message, "No id found");
    }
  }


  @Override
  public void processInboxMessage(Message message) {
    // this is where we process messages sent directly to us
    // are we being asked to shutdown? change logging? change instrumentation? report status?
    System.out.println("Received inbox message: " + message);
  }


  @Override
  public void processCoherenceMessage(Message message) {
    // this is where we process coherence messages sent between service
    // instances to coordinate our activities.
    System.out.println("Received coherence message: " + message);
  }

}
