package coyote.kestrel.service;

import coyote.dataframe.DataFrame;
import coyote.kestrel.protocol.KestrelProtocol;

public class ServiceUtil {

  public static String getCommand(DataFrame request){
    String retval = null;
    if( request != null){
      retval = request.getAsString(KestrelProtocol.COMMAND_FIELD);
    } else {
      throw new IllegalArgumentException("Request frame was null");
    }
    return retval;
  }

  public static String getIdentifier(DataFrame request){
    String retval = null;
    if( request != null){
      retval = request.getAsString(KestrelProtocol.ID_FIELD);
    } else {
      throw new IllegalArgumentException("Request frame was null");
    }
    return retval;
  }

}
