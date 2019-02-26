package coyote;

import coyote.kestrel.transport.Message;
import coyote.kestrel.transport.Transport;
import coyote.kestrel.transport.TransportBuilder;

public class RabbitTransport {

  public static void main(String[] args) {

    TransportBuilder builder = new TransportBuilder()
            .setScheme("amqp")
            .setUsername("guest")
            .setPassword("guest")
            .setHost("localhost")
            .setPort(5672)
            .setConnectionTimeout(500)
            .setVirtualHost("virtualHost");

    Transport transport = builder.build();

    Message message = new Message();
    message.setGroup("Service.Queue");
    message.setReplyGroup("My.Inbox");

  }
}
