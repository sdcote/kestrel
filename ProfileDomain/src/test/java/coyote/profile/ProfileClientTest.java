package coyote.profile;


import coyote.kestrel.client.ClientBuilder;
import org.junit.jupiter.api.*;

public class ProfileClientTest {




  @DisplayName("Single test successful")
  @Test
  void testClientBuilder() {

    // Setup a client which uses a mock transport

    ProfileClient client = ClientBuilder.build(ProfileClient.class);


    System.out.println("Success");


  }


}
