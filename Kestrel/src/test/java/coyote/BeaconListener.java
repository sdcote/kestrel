package coyote;

import coyote.kestrel.transport.*;
import coyote.loader.log.Log;

public class BeaconListener implements MessageListener {


  /**
   * The call-back used to receive messages.
   *
   * <p>Messages arrive in real time; we don't have to retrieve them.</p>
   *
   * @param msg the next message received from the message transport.
   */
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

    transport.open();


    MessageTopic topic = transport.getTopic("BEACON");
    topic.attach(new BeaconListener());



    // run for 5 minutes
    try {
      Thread.sleep(300000);
    } catch (final InterruptedException ignore) {
    }

    transport.close();

    Log.info("Done");
  }

}
