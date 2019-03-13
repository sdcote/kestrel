package coyote.profile;

import coyote.kestrel.client.AbstractClient;
import coyote.loader.cfg.Config;

public class DefaultProfileClient extends AbstractClient implements ProfileClient {
  Config config = null;

  @Override
  public Profile retrieveProfile(String id) {
    return new Profile();
  }
}
