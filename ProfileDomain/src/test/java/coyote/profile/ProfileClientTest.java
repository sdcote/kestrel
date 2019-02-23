package coyote.profile;


import org.junit.jupiter.api.*;

public class ProfileClientTest {




  @DisplayName("Single test successful")
  @Test
  void testMockClient() {

    // Setup a client which uses a mock transport

    ProfileClient client = ProfileFactory.createClient();


    System.out.println("Success");


  }


}
