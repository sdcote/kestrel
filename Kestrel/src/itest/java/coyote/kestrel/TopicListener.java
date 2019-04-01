package coyote.kestrel;

import coyote.dataframe.marshal.JSONMarshaler;
import coyote.kestrel.protocol.KestrelProtocol;
import coyote.kestrel.transport.*;
import coyote.loader.log.Log;

public class TopicListener implements MessageListener {


  /**
   * @param args
   */
  public static void main(final String[] args) {

    Transport transport = new TransportBuilder().setURI("amqp://guest:guest@localhost:5672").build();
    transport.open();

    MessageTopic topic = transport.getTopic(KestrelProtocol.HEARTBEAT_GROUP);
    topic.attach(new TopicListener());

    // run for 5 minutes
    try {
      Thread.sleep(300000);
    } catch (final InterruptedException ignore) {
    }

    transport.close();

    Log.info("Done");
  }

  /**
   * The call-back used to receive messages.
   *
   * <p>Messages arrive in real time; we don't have to retrieve them.</p>
   *
   * @param msg the next message received from the message transport.
   */
  public void onMessage(Message msg) {
    System.out.println(JSONMarshaler.toFormattedString(msg));
  }

}
