package coyote;

import coyote.kestrel.transport.Message;
import coyote.kestrel.transport.MessageConsumer;
import coyote.kestrel.transport.Transport;
import coyote.kestrel.transport.TransportBuilder;
import coyote.loader.log.Log;

public class BeaconListener implements MessageConsumer {


  public void onMessage(Message msg) {
    System.out.println(msg.getGroup() + ": " + msg.getPayload());
  }


  /**
   * @param args
   */
  public static void main(final String[] args) {


    Transport transport = new TransportBuilder()
            .setScheme("amqp")
            .setUsername("guest")
            .setPassword("guest")
            .setHost("localhost")
            .setPort(5672)
            .build();

    transport.attach(new BeaconListener(), "DATE");

    // run for 5 minutes
    try {
      Thread.sleep(300000);
    } catch (final InterruptedException e) {
      e.printStackTrace();
    }

    Log.info("Done");
  }

}
