package coyote.profile;


import coyote.kestrel.proxy.ClientRegistry;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ProxyBuilderTest {


  @DisplayName("Simple proxy locate successful")
  @Ignore
  void testClientBuilder() {
    ClientRegistry registry = new ClientRegistry();

    registry.addServiceProxyClass(ProfileProxy.class);

    registry
            .setScheme("amqp")
            .setUsername("guest")
            .setPassword("guest")
            .setHost("localhost")
            .setPort(5672);

    ProfileClient client = registry.locate(ProfileClient.class);
    assertNotNull(client);

    // client.retrieveProfile("123");
  }


}
