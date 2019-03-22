package coyote.kestrel.proxy;

import coyote.kestrel.transport.Message;
import coyote.kestrel.transport.Transport;
import coyote.kestrel.transport.TransportBuilder;
import org.junit.jupiter.api.*;

import java.io.IOException;

public class ProxySendDirectTest extends ProxyTestBase {

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

  @DisplayName("Simple send")
  @Test
  void sendOne() throws IOException {
    TestClient client = new TestProxy();
    Transport transport = createTestTransport();
    transport.open();
    client.setTransport(transport);
    client.initialize();
    client.sendDirect(new Message());
    transport.close();
  }


  /**
   * Disabled test to allow running test cases in the IDE; will not run as part of the build
   *
   * @throws IOException
   */
  @Test
  @Disabled("Send many requests")
  void sendMany() throws IOException {
    TestClient client = new TestProxy();
    Transport transport = new TransportBuilder().setURI("amqp://guest:guest@localhost:5672").build();
    transport.open();
    client.setTransport(transport);
    client.initialize();
    int count = 100;
    long start = System.currentTimeMillis();
    client.sendMany("SVC.PROFILE", count);
    long elapsed = System.currentTimeMillis() - start;
    System.out.println("Send " + count + " messages in " + elapsed + " milliseconds");
    transport.close();
  }

}
