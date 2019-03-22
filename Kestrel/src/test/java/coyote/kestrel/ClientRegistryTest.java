package coyote.kestrel;


import coyote.kestrel.proxy.ClientRegistry;
import coyote.kestrel.proxy.TestClient;
import coyote.kestrel.proxy.TestProxy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ClientRegistryTest {


  @DisplayName("Simple proxy locate successful")
  @Test
  void testClientBuilder() {
    ClientRegistry registry = new ClientRegistry();
    registry.setURI("amqp://guest:guest@localhost:5672");
    registry.addServiceProxyClass(TestProxy.class);
    TestClient client = registry.locate(TestClient.class);
    assertNotNull(client);
  }


}
