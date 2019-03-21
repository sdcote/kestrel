package coyote.profile;


import coyote.commons.WebServer;
import coyote.commons.network.http.HTTPSession;
import coyote.commons.network.http.Response;
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
//    server.addServiceProxyClass(ProfileClient.class,null);

    // find an instance of the service proxy
    ProfileClient client = server.locateProxy(ProfileClient.class);
    if( client == null ){
      Log.error("could not retrieve Profile Service proxy");
    }

    String name = coyote.profile.ProfileProxy.class.getSimpleName();


    // The results data frame is where our superclass generates its responce
    getResults().merge(new DataFrame().set("id",id).set("name", "Bob").set("msg", "Hello World!"));

    // create a response using the superclass methods
    return Response.createFixedLengthResponse(getStatus(), getMimeType(), getText());
  }

}
