package coyote.kestrel.transport.amqp;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import coyote.kestrel.transport.MessageListener;
import coyote.kestrel.transport.MessageTopic;

import java.io.IOException;

/**
 * AMQP messages are sent to an exchange type of "TOPIC" and the routing key
 * is used to control which queues receive messages.
 */
public class AmqpTopic extends AmqpChannel implements MessageTopic {


  private static final String EXCHANGE_NAME = "TOPIC";

  public AmqpTopic(Channel channel, String name) {
    setChannel(channel);
    setName(name);
  }

  @Override
  public void attach(MessageListener consumer) {

    try {
      String queueName = getChannel().queueDeclare().getQueue();
      getChannel().queueBind(queueName, EXCHANGE_NAME, "");

      System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

      DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), "UTF-8");
        System.out.println(" [x] Received '" + message + "'");
      };
      getChannel().basicConsume(queueName, true, deliverCallback, consumerTag -> {
      });
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  @Override
  public void detach(MessageListener consumer) {

  }


}
