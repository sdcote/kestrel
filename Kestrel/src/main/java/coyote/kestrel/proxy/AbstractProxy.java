package coyote.kestrel.proxy;

import coyote.loader.log.Log;

/**
 * The base class for all service proxies
 */
public class AbstractProxy implements KestrelProxy {
  public AbstractProxy(){
    Log.info("proxy initializing");
  }


}
