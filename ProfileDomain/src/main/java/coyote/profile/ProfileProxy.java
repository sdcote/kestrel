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

  @Override
  public Profile retrieveProfile(String id) {
    Profile retval = null;
    Message request = createMessage(ProfileProtocol.PROFILE_GROUP);
    request.setPayload(new DataFrame().set("CMD", "Get").set("ID",id));

    // send the request and wait up to the time-out interval for responses.
    ResponseFuture response = sendAndWait(request,3000);
    List<Message> responses = response.getResponses();
    if( responses.size()>0){
      retval = new Profile(responses.get(0).getPayload());
    }else {
      Log.debug("profile '"+id+"' was not found");
    }

    return retval;
  }



}
