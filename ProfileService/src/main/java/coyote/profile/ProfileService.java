package coyote.profile;


import coyote.dataframe.DataFrame;
import coyote.kestrel.service.AbstractService;
import coyote.kestrel.transport.Message;
import coyote.loader.log.Log;

public class ProfileService extends AbstractService {


  private static final String GROUP_NAME = "SVC.PROFILE";


  @Override
  public String getGroupName() {
    return GROUP_NAME;
  }


  /**
   * This is where we receive our request messages.
   *
   * <p>If we throw an exception, the message will be negatively acknowledged
   * at the transport level and re-queued for delivery.</p>
   *
   * @param message
   */
  @Override
  public void process(Message message) {
    Log.info("Received service message: " + message);

    DataFrame payload = message.getPayload();
    if (payload.contains("ID")) {
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
    Log.info("Received inbox message: " + message);
  }


  @Override
  public void processCoherenceMessage(Message message) {
    // this is where we process coherence messages sent between service
    // instances to coordinate our activities.
    Log.info("Received coherence message: " + message);
  }

}
