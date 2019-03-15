package coyote.profile;


import coyote.kestrel.proxy.ClientRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ClientRegistryTest {


  @DisplayName("Simple proxy locate successful")
  @Test
  void testClientBuilder() {
    ClientRegistry registry = new ClientRegistry();

    registry.addServiceProxyClass(ProfileProxy.class);

    registry.setScheme("amqp")
            .setUsername("guest")
            .setPassword("guest")
            .setHost("localhost")
            .setPort(5672);

    ProfileClient client = registry.locate(ProfileClient.class);
    assertNotNull(client);
    client.retrieveProfile("123");
  }


}
