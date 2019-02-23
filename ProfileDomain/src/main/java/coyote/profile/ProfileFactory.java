package coyote.profile;

public class ProfileFactory {

  public static ProfileClient createClient(){
    return new DefaultProfileClient();
  }
}
