package coyote.kestrel.http;

import coyote.commons.network.MimeType;
import coyote.commons.network.http.HTTPSession;
import coyote.commons.network.http.Response;
import coyote.commons.network.http.Status;
import coyote.commons.network.http.responder.DefaultResponder;
import coyote.commons.network.http.responder.Resource;
import coyote.commons.network.http.responder.Responder;
import coyote.dataframe.DataFrame;
import coyote.dataframe.marshal.JSONMarshaler;
import coyote.dataframe.marshal.XMLMarshaler;

import java.util.Map;

public abstract class AbstractKestrelResponder extends DefaultResponder implements Responder {

  protected Status status = Status.OK;
  protected DataFrame results = new DataFrame();
  protected MimeType mimetype = MimeType.JSON;


  @Override
  public Response delete(Resource resource, Map<String, String> urlParams, HTTPSession session) {
    return Response.createFixedLengthResponse(getStatus(), getMimeType(), getText());
  }


  @Override
  public Response get(Resource resource, Map<String, String> urlParams, HTTPSession session) {
    return Response.createFixedLengthResponse(getStatus(), getMimeType(), getText());
  }


  @Override
  public Response other(String method, Resource resource, Map<String, String> urlParams, HTTPSession session) {
    return Response.createFixedLengthResponse(getStatus(), getMimeType(), getText());
  }


  @Override
  public Response post(Resource resource, Map<String, String> urlParams, HTTPSession session) {
    return Response.createFixedLengthResponse(getStatus(), getMimeType(), getText());
  }


  @Override
  public Response put(Resource resource, Map<String, String> urlParams, HTTPSession session) {
    return Response.createFixedLengthResponse(getStatus(), getMimeType(), getText());
  }


  @Override
  public Status getStatus() {
    return status;
  }

  /**
   * @param status the status to set
   */
  protected void setStatus(Status status) {
    this.status = status;
  }

  @Override
  public String getText() {
    if (mimetype.equals(MimeType.XML)) {
      return XMLMarshaler.marshal(results);
    } else {
      return JSONMarshaler.marshal(results);
    }
  }

  @Override
  public String getMimeType() {
    return mimetype.getType();
  }

  /**
   * @return the results
   */
  protected DataFrame getResults() {
    return results;
  }

  /**
   * @param results the results to set
   */
  protected void setResults(DataFrame results) {
    this.results = results;
  }

  /**
   * @param type the mimetype to set
   */
  protected void setMimetype(MimeType type) {
    mimetype = type;
  }

}