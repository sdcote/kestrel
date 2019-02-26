/*
 * Copyright Stephan D. Cote' 2008 - All rights reserved.
 */
package coyote;

import coyote.dataframe.DataFrame;
import coyote.kestrel.transport.Message;
import coyote.kestrel.transport.MessageChannel;
import coyote.kestrel.transport.Transport;
import coyote.kestrel.transport.TransportBuilder;

import java.util.Date;


/**
 * The Beacon class shows how to send messages
 */
public class Beacon {

  public static void main(final String[] args) throws Exception {

    Transport transport = new TransportBuilder()
            .setScheme("amqp")
            .setUsername("guest")
            .setPassword("guest")
            .setHost("localhost")
            .setPort(5672)
            .build();

    MessageChannel topic = transport.getTopic("BEACON");


    do {
      Message msg = new Message();
      msg.setPayload(new DataFrame().set("DATE", new Date().toString()));
      topic.send(msg);

      try {
        Thread.sleep(10000);
      } catch (final Exception ignore) {
      }
    }
    while (true);
  }

}
