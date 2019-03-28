package coyote.profile;


import coyote.kestrel.protocol.ResponseFuture;
import coyote.kestrel.transport.StatUtil;
import coyote.kestrel.transport.Transport;
import coyote.kestrel.transport.TransportBuilder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ProfileClientTest {


  @DisplayName("Simple performance test")
  @Disabled("no running broker to use")
  //@Test
  void testClient() {
    ProfileClient client = new ProfileProxy();
    Transport transport = new TransportBuilder().setURI("amqp://guest:guest@localhost:5672").build();
    transport.open();
    client.setTransport(transport);
    client.initialize();

    // enable timing se we can see performance statistics
    client.getStatBoard().enableTiming(true);

    // run several requests, one after another
    for (int x = 0; x < 10; x++) {
      Profile profile = client.retrieveProfile("123");
      System.out.println(profile);
    }

    // show the performance statistics
    System.out.println(StatUtil.dump(client.getStatBoard()));
    transport.close();
  }

  @DisplayName("Parallelized performance test")
  @Disabled("no running broker to use")
  void testClientFutures() {
    ProfileProxy client = new ProfileProxy();
    Transport transport = new TransportBuilder().setURI("amqp://guest:guest@localhost:5672").build();
    transport.open();
    client.setTransport(transport);
    client.initialize();

    // enable timing se we can see performance statistics
    client.getStatBoard().enableTiming(true);

    List<ResponseFuture> responses = new ArrayList<>();
    for (int x = 0; x < 10; x++) {
      ResponseFuture future = client.retrieveProfileFuture("123");
      future.setTimeout(5000); // expire after a few seconds
      responses.add(future);
    }
    System.out.println(responses.size() + " requests sent, collecting responses...");
    while (responses.size() > 0) {
      Iterator<ResponseFuture> i = responses.iterator();
      while (i.hasNext()) {
        ResponseFuture response = i.next();
        if (!response.isWaiting() || response.isExpired()) {
          response.close();
          i.remove();
        }
      }
      try {
        Thread.sleep(10);
      } catch (InterruptedException ignore) {
        // ignore
      }
    }
    System.out.println("Done.");

    // show the performance statistics
    System.out.println(StatUtil.dump(client.getStatBoard()));
    transport.close();
  }

}
