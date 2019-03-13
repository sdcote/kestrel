package coyote.profile;


import coyote.kestrel.proxy.ProxyBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ProfileClientTest {


  @DisplayName("Single test successful")
  @Test
  void testClientBuilder() {

    ProxyBuilder.addProxyClass(ProfileProxy.class);

    ProxyBuilder
            .setScheme("amqp")
            .setUsername("guest")
            .setPassword("guest")
            .setHost("localhost")
            .setPort(5672)
            .setConnectionTimeout(500)
            .setQuery("/vhost");

    ProxyBuilder.build(ProfileClient.class);


    System.out.println("Success");


  }


}
