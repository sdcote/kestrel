/*
 * Copyright Stephan D. Cote' 2008 - All rights reserved.
 */
package coyote;

import coyote.dataframe.DataFrame;
import coyote.kestrel.transport.Message;
import coyote.kestrel.transport.MessageChannel;
import coyote.kestrel.transport.Transport;
import coyote.kestrel.transport.TransportBuilder;

import java.io.IOException;
import java.util.Date;


/**
 * The Beacon class shows how to send messages
 */
public class Beacon {

  public static void main(final String[] args) {

    Transport transport = new TransportBuilder().setURI("amqp://guest:guest@localhost:5672").build();
    transport.open(); // connects to the broker

    // Create a message channel with a name of BEACON. If you want messages on this topic, you will need to attach a
    // listener to another topic channel with the same name
    MessageChannel topic = transport.getTopic("BEACON");


    Message msg = new Message(); // reusable message
    DataFrame payload = new DataFrame(); // reusable payload

    do {
      payload.put("DATE", new Date().toString()); // change the payload
      msg.setPayload(payload); // serialize the payload into the message

      try {
        topic.send(msg); // send the message on the topic
      } catch (IOException e) {
        e.printStackTrace();
      }

      // sleep for a short while
      try {
        Thread.sleep(10000);
      } catch (final Exception ignore) {
      }
    }
    while (true);
  }

}
