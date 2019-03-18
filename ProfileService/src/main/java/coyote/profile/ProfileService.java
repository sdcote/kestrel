package coyote.profile;


import coyote.commons.StringUtil;
import coyote.dataframe.DataFrame;
import coyote.dataframe.marshal.JSONMarshaler;
import coyote.kestrel.KestrelProtocol;
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

    DataFrame request = message.getPayload();
    if (StringUtil.isNotEmpty(message.getId())) {

     Message response = KestrelProtocol.createResponse(message);

      // create a result of our processing
      DataFrame result = new DataFrame();
      result.put("ResponseCode", 203);
      response.setPayload(result);

      // send the response
      try {
        Log.info("Sending response message: "+response);
        send(response);
        Log.info("Sent response to group: "+response.getGroup());
      } catch (IOException e) {
        Log.error("Could not send response: "+e.getMessage());
      }

    } else {
      Log.error("No id found in received message");
    }
  }

}
