/*
 * Copyright Stephan D. Cote' 2008 - All rights reserved.
 */
package coyote;

import coyote.dataframe.DataFrame;
import coyote.kestrel.transport.Message;
import coyote.kestrel.transport.MessageTopic;
import coyote.kestrel.transport.Transport;
import coyote.kestrel.transport.TransportBuilder;

import java.io.IOException;
import java.util.Date;


/**
 * The Beacon class shows how to send messages
 */
public class Beacon {

  public static void main(final String[] args) {

    // Configure the transport builder
    Transport transport = new TransportBuilder().setURI("amqp://guest:guest@localhost:5672").build();

    // connects to the broker
    transport.open();

    // Create a message group which will allow multiple consumers
    MessageTopic topic = transport.getTopic("BEACON");

    // reusable message
    Message message = new Message();

    // reusable payload
    DataFrame payload = new DataFrame();

    // publish a message on the topic every 5 seconds
    do {
      // change the payload
      payload.put("DATE", new Date().toString());
      // serialize the payload into the message
      message.setPayload(payload);

      // send the message on the topic
      try {
        topic.send(message);
      } catch (IOException e) {
        e.printStackTrace();
      }

      // sleep for a short while
      try {
        Thread.sleep(5000);
      } catch (final Exception ignore) {
      }
    }
    while (true);
  }

}
