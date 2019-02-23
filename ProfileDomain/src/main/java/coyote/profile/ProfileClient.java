package coyote.profile;


public interface ProfileClient {

  /**
   * Retrieve the profile with the given identifier.
   *
   * @param id the identifier of the profile to retrieve
   * @return the Profile with the diven identifier or null if not found
   */
  public Profile retrieveProfile(String id);
}
