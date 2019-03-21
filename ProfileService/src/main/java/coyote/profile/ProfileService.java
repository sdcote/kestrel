package coyote.profile;


import coyote.dataframe.DataFrame;
import coyote.dataframe.marshal.JSONMarshaler;
import coyote.kestrel.service.AbstractService;
import coyote.kestrel.service.ServiceUtil;
import coyote.kestrel.transport.Message;
import coyote.loader.log.Log;

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
   * @param message the message we received from the message group
   */
  @Override
  public void process(Message message) {
    Log.debug("Received service message: " + JSONMarshaler.toFormattedString(message));

    // extract the message payload
    DataFrame request = message.getPayload();

    // get the command from the request payload
    String cmd = ServiceUtil.getCommand(request);

    // Each service can support any number of commands
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


  /**
   * This only returns one profile, ID=123.
   *
   * @param request the payload of the message representing the request
   * @return the profile or null if the identified profile could not be found
   */
  private DataFrame getProfile(DataFrame request) {
    DataFrame retval = null;
    String id = ServiceUtil.getIdentifier(request);
    if ("123".equals(id)) {
      retval = new DataFrame().set("id", "123").set("theme", "dark").set("logging", "error,fatal,warn,notice").set("name", "Bob");
    }
    return retval;
  }

}
