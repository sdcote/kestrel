package coyote.profile;

import coyote.dataframe.DataFrame;
import coyote.kestrel.protocol.KestrelProtocol;
import coyote.kestrel.protocol.ResponseFuture;
import coyote.kestrel.proxy.AbstractProxy;
import coyote.kestrel.transport.Message;
import coyote.loader.log.Log;

import java.io.IOException;
import java.util.List;

/**
 * Implements the service proxy for the profile service.
 *
 * <p>There is not very much logic in this class. Methods only create a message
 * (through its superclass), populate the message with a few fields, then send
 * the message receiving a {@code ResponseFuture} as a return value.</p>
 *
 * <p>The {@code ResponseFuture} contains the state if the request in the
 * framework and eventually the response messages sent from services or other
 * components in the application.</p>
 *
 * <p>This example uses the {@sendAndWait(DataFrame,int)} method in the super-
 * class to block for at least one response before returning.</p>
 *
 * <p>Just create an instance of this class in your runtime to implement a
 * local reference to a remote service.</p>
 */
public class ProfileProxy extends AbstractProxy implements ProfileClient {


  /**
   * Retrieve the profile with the given identifier.
   *
   * <p>This implementation of the interface method builds a request message
   * from the arguments and send the request on the appropriate message group.
   * The thread then waits for a response and generates a return value from
   * the data received.</p>
   *
   * @param id the identifier of the profile to retrieve
   * @return the profile with the given identifier or null if the profile could not be found.
   */
  @Override
  public Profile retrieveProfile(String id) {
    Profile retval = null;
    Message request = super.createMessage(ProfileProtocol.PROFILE_GROUP);
    request.setPayload(new DataFrame().set(KestrelProtocol.COMMAND_FIELD, ProfileProtocol.GET_CMD).set(ProfileProtocol.ID_FIELD, id));
    ResponseFuture response = super.sendAndWait(request, 3000);
    List<Message> responses = response.getResponses();
    if (responses.size() > 0) {
      retval = new Profile(responses.get(0).getPayload()); // use just the first response
    } else {
      Log.debug("profile '" + id + "' was not found");
    }
    return retval;
  }


  /**
   * This method is only used for testing and not strictly required.
   *
   * @param id the identifier of the profile to retrieve
   * @return the ResponseFuture representing the request.
   */
  public ResponseFuture retrieveProfileFuture(String id) {
    ResponseFuture retval = null;
    Message request = createMessage(ProfileProtocol.PROFILE_GROUP);
    request.setPayload(new DataFrame().set(KestrelProtocol.COMMAND_FIELD, ProfileProtocol.GET_CMD).set(ProfileProtocol.ID_FIELD, id));
    try {
      retval = send(request);
    } catch (IOException e) {
      Log.error(e);    }
    return retval;
  }

}
