package coyote.profile;


import coyote.kestrel.proxy.KestrelProxy;

public interface ProfileClient extends KestrelProxy {

  /**
   * Retrieve the profile with the given identifier.
   *
   * @param id the identifier of the profile to retrieve
   * @return the Profile with the given identifier or null if not found
   */
   Profile retrieveProfile(String id);


}
