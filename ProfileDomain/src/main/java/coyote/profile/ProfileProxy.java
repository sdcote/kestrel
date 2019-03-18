package coyote.profile;

import coyote.dataframe.DataFrame;
import coyote.kestrel.protocol.ResponseFuture;
import coyote.kestrel.proxy.AbstractProxy;
import coyote.kestrel.proxy.KestrelProxy;
import coyote.kestrel.transport.Message;
import coyote.loader.cfg.Config;

/**
 * Implements the service proxy for the profile service.
 */
public class ProfileProxy extends AbstractProxy implements ProfileClient {

  @Override
  public Profile retrieveProfile(String id) {

    Message request = createMessage(ProfileProtocol.PROFILE_GROUP);
    request.setPayload(new DataFrame().set("Request","Ping);"));

    ResponseFuture response = new ResponseFuture();

    // send(response);

    return new Profile();
  }


}
