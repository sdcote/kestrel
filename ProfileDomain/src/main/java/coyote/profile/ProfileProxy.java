package coyote.profile;

import coyote.kestrel.proxy.AbstractProxy;
import coyote.loader.cfg.Config;

/**
 * Implements the service proxy for the profile service.
 */
public class ProfileProxy extends AbstractProxy implements ProfileClient {

  @Override
  public Profile retrieveProfile(String id) {
    return new Profile();
  }

}
