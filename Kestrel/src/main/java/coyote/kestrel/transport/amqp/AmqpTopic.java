package coyote.kestrel.transport.amqp;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import coyote.kestrel.transport.Message;
import coyote.kestrel.transport.MessageListener;
import coyote.kestrel.transport.MessageTopic;

import java.io.IOException;

/**
 * AMQP messages are sent to an exchange type of "TOPIC" and the routing key
 * is used to control which queues receive messages.
 */
public class AmqpTopic extends AmqpChannel implements MessageTopic {



  public AmqpTopic(Channel channel, String name) {
    setChannel(channel);
    setName(name);
  }

  @Override
  public void attach(MessageListener consumer) {

    try {
      String queueName = getChannel().queueDeclare().getQueue();
      getChannel().queueBind(queueName, AmqpTransport.TOPIC_EXCHANGE, getName());

      DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), "UTF-8");
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


  @Override
  public void send(Message message) throws IOException {
    if (getChannel() != null) {
      getChannel().basicPublish(AmqpTransport.TOPIC_EXCHANGE, getName(), null, message.getBytes());
    } else {
      throw new IOException("No channel set");
    }
  }


}
