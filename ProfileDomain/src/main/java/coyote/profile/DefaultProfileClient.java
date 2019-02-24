package coyote.profile;

import coyote.loader.cfg.Config;

public class DefaultProfileClient implements ProfileClient {
  Config config = null;

  @Override
  public Profile retrieveProfile(String id) {
    return new Profile();
  }
}
