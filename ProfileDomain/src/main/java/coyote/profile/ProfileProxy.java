package coyote.profile;

import coyote.dataframe.DataFrame;
import coyote.kestrel.protocol.ResponseFuture;
import coyote.kestrel.proxy.AbstractProxy;
import coyote.kestrel.transport.Message;
import coyote.loader.log.Log;

import java.io.IOException;
import java.util.List;

/**
 * Implements the service proxy for the profile service.
 */
public class ProfileProxy extends AbstractProxy implements ProfileClient {


  /**
   * Retrieve the profile with the given identifier
   *
   * @param id the identifier of the profile to retrieve
   * @return the profile with the given identifier or null if the profile could not be found.
   */
  @Override
  public Profile retrieveProfile(String id) {
    Profile retval = null;
    Message request = createMessage(ProfileProtocol.PROFILE_GROUP);
    request.setPayload(new DataFrame().set("CMD", "Get").set("ID", id));
    ResponseFuture response = sendAndWait(request, 3000);
    List<Message> responses = response.getResponses();
    if (responses.size() > 0) {
      retval = new Profile(responses.get(0).getPayload());
    } else {
      Log.debug("profile '" + id + "' was not found");
    }
    return retval;
  }

  public ResponseFuture retrieveProfileFuture(String id) {
    ResponseFuture retval = null;
    Message request = createMessage(ProfileProtocol.PROFILE_GROUP);
    request.setPayload(new DataFrame().set("CMD", "Get").set("ID", id));
    try {
      retval = send(request);
    } catch (IOException e) {
      Log.error(e);    }
    return retval;
  }

}
