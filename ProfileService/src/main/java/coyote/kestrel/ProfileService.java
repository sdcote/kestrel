package coyote.kestrel;


import coyote.loader.log.LogMsg;

public class ProfileService extends AbstractService implements KestrelService {


  public static final LogMsg.BundleBaseName MSG;
  static {
    MSG = new LogMsg.BundleBaseName("ProfileMsg");
  }


  /**
   * This is where we receive our requests.
   *
   * @param packet
   */
  @Override
  public void process(Packet packet){

  }



}
