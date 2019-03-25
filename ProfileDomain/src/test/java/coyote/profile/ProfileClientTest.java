package coyote.profile;


import coyote.i13n.StatBoard;
import coyote.kestrel.transport.StatUtil;
import coyote.kestrel.transport.Transport;
import coyote.kestrel.transport.TransportBuilder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ProfileClientTest {


  @DisplayName("Simple performance successful")
  @Disabled("no running broker to use")
  void testClient() {
    ProfileClient client = new ProfileProxy();
    Transport transport = new TransportBuilder().setURI("amqp://guest:guest@localhost:5672").build();
    transport.open();
    client.setTransport(transport);
    client.initialize();

    // enable timing se we can see performance statistics
    client.getStatBoard().enableTiming(true);

    // run several requests, one after another
    for ( int x = 0;x<10;x++ ) {
      Profile profile = client.retrieveProfile("123");
      //System.out.println(profile);
    }

    // show the performance statistics
    System.out.println(StatUtil.dump(client.getStatBoard()));
    transport.close();
  }

}
