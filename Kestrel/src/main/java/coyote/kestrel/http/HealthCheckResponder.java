package coyote.kestrel.http;

import coyote.commons.network.MimeType;
import coyote.commons.network.http.HTTPSession;
import coyote.commons.network.http.Response;
import coyote.commons.network.http.responder.Resource;
import coyote.commons.network.http.responder.Responder;

import java.util.Map;


/**
 * This responder reports the health of the component.
 *
 * <p>It is designed to be called repeatedly. If anything else but a 200 status
 * code is received, the client can assume there are problems.
 */
public class HealthCheckResponder extends AbstractKestrelResponder implements Responder {

  @Override
  public Response get(Resource resource, Map<String, String> urlParams, HTTPSession session) {
    return Response.createFixedLengthResponse(getStatus(), MimeType.TEXT.getType(), "UP");
  }

}