package coyote.kestrel.client;

import coyote.loader.log.Log;

/**
 * The base class for all clients
 */
public class AbstractClient implements KestrelClient{
  public AbstractClient(){
    Log.info("client initializing");
  }


}
