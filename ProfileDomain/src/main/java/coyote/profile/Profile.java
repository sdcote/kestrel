package coyote.profile;

import coyote.dataframe.DataFrame;

/**
 * This represents an identified profile
 */
public class Profile extends DataFrame {


  public Profile(DataFrame frame) {
    merge(frame);
  }

}
