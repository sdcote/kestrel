package coyote.kestrel.proxy;

import coyote.kestrel.transport.Message;
import coyote.kestrel.transport.Transport;
import coyote.kestrel.transport.TransportBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ProxySendDirectTest extends AbstractProxyTest {

  private ProxySendDirectTest() {
  }

  @BeforeAll
  static void setup() {
    System.out.println("@BeforeAll - executes once before all test methods in this class");
  }

  @AfterAll
  static void done() {
    System.out.println("@AfterAll - executed after all test methods.");
  }

  @DisplayName("Single test successful")
  @Test
  void testSingleSuccessTest() {
    TestClient client = new TestProxy();
    Transport transport = createTestTransport();
    transport.open();
    client.setTransport(transport);
    client.initialize();

    client.sendDirect(new Message() );
  }


}
