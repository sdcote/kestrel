package coyote.profile;


import coyote.commons.network.http.HTTPSession;
import coyote.commons.network.http.Response;
import coyote.commons.network.http.Status;
import coyote.commons.network.http.responder.AbstractJsonResponder;
import coyote.commons.network.http.responder.Resource;
import coyote.commons.network.http.responder.Responder;
import coyote.dataframe.DataFrame;
import coyote.kestrel.http.Server;
import coyote.loader.cfg.Config;
import coyote.loader.log.Log;

import java.util.Map;

/**
 * This class responds to HTTP requests.
 *
 * <p>A new instance of this class is created for each request. There is no
 * concept of state between calls except for what is stored in the web server
 * the reference to which is passed to the method invocations.</p>
 */
public class ProfileWebService extends AbstractJsonResponder implements Responder {

  /**
   * Called to handle the HTTP GET request.
   */
  @Override
  public Response get(Resource resource, Map<String, String> urlParams, HTTPSession session) {
    Server server = resource.initParameter(0, Server.class);
    Config config = resource.initParameter(1, Config.class);

    // Get the command from the URL parameters specified when we were registered with the router
    String id = urlParams.get("id");

    // Make sure the server knows about the service proxy class, use our configuration object
    server.addServiceProxyClass(ProfileProxy.class, config);

    // get a configured instance of the service proxy
    ProfileClient client = server.locateProxy(ProfileClient.class);


    if (client == null) {
      Log.error("could not retrieve Profile Service proxy");
      super.setStatus(Status.INTERNAL_ERROR);
      super.getResults().merge(new DataFrame("Error", "Could not retrieve proxy"));
    } else {
      // make the service call
      Profile profile = client.retrieveProfile(id);

      // Set the results
      if (profile != null) {
        super.getResults().merge(profile);
        super.setStatus(Status.OK);
      } else {
        super.setStatus(Status.NOT_FOUND);
      }
    }

    // create a response using the superclass methods
    return Response.createFixedLengthResponse(getStatus(), getMimeType(), getText());
  }

}
