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

    Transport transport = new TransportBuilder().setURI("amqp://guest:guest@localhost:5672").build();
    transport.open(); // connects to the broker

    MessageChannel topic = transport.getTopic("BEACON");


    do {
      Message msg = new Message();
      msg.getPayload().put("DATE", new Date().toString());
      topic.send(msg);
System.out.println("SENT: "+ msg.toString());
      try {
        Thread.sleep(10000);
      } catch (final Exception ignore) {
      }
    }
    while (true);
  }

}
