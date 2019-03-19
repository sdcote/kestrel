package coyote.profile;


import coyote.dataframe.DataFrame;
import coyote.dataframe.marshal.JSONMarshaler;
import coyote.kestrel.protocol.KestrelProtocol;
import coyote.kestrel.service.AbstractService;
import coyote.kestrel.service.ServiceUtil;
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


    // figure out what the service is supposed to do
    DataFrame request = message.getPayload();

    String cmd = ServiceUtil.getCommand(request);

    DataFrame result;
    if (cmd != null) {
      switch (cmd.toUpperCase()) {
        case "GET":
          sendAck(message, getProfile(request));
          break;
        default:
          sendNak(message, "unsupported command");
          break;
      }
    } else {
      sendNak(message, "No command found in request");
    }

  }


  private DataFrame getProfile(DataFrame request) {
    String id = ServiceUtil.getIdentifier(request);
    return null;
  }

}
