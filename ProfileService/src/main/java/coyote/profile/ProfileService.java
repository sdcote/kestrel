package coyote.profile;


import coyote.commons.StringUtil;
import coyote.dataframe.DataFrame;
import coyote.dataframe.marshal.JSONMarshaler;
import coyote.kestrel.service.AbstractService;
import coyote.kestrel.transport.Message;
import coyote.loader.log.Log;

import java.io.IOException;

public class ProfileService extends AbstractService {


  private static final String GROUP_NAME = ProfileProtocol.PROFILE_GROUP;


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
    Log.info("Received service message: " + JSONMarshaler.toFormattedString(message));

    DataFrame payload = message.getPayload();
    if (StringUtil.isNotEmpty(message.getId())) {
      // create a response message
      Message response = message.createResponse();

      // create a result of our processing
      DataFrame result = new DataFrame();
      result.put("ResponseCode", 203);
      response.setPayload(result);

      // send the response
      try {
        Log.info("Sending response message: "+response);
        send(response);
      } catch (IOException e) {
        Log.error("Could not send response: "+e.getMessage());
      }

      Log.info("Sent response to inbox: "+response.getGroup());
    } else {
      Log.error("No id found in received message");
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
