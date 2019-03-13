package coyote.profile;


import coyote.kestrel.proxy.ProxyBuilder;
import org.junit.jupiter.api.*;

public class ProfileClientTest {




  @DisplayName("Single test successful")
  @Test
  void testClientBuilder() {

    ProxyBuilder.addProxyClass(ProfileProxy.class);

    // Setup a proxy which uses a mock transport

    ProfileClient client = ProxyBuilder.build(ProfileClient.class);


    System.out.println("Success");


  }


}
