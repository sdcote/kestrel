package coyote.profile;


import coyote.kestrel.transport.Transport;
import coyote.kestrel.transport.TransportBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ProfileClientTest {


  @DisplayName("Simple test successful")
  @Test
  void testClient() {
    ProfileClient client = new ProfileProxy();
    Transport transport = new TransportBuilder().setURI("amqp://guest:guest@localhost:9999").build();
    transport.open();
    client.setTransport(transport);
    client.retrieveProfile("123");
    transport.close();
  }


}
