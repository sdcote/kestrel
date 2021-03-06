package coyote.kestrel.transport.amqp;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import coyote.kestrel.protocol.MessageCodec;
import coyote.kestrel.transport.Message;
import coyote.kestrel.transport.MessageListener;
import coyote.kestrel.transport.MessageTopic;
import coyote.loader.log.Log;

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
  public void attach(MessageListener listener) {
    try {
      String queueName = getChannel().queueDeclare().getQueue();
      getChannel().queueBind(queueName, AmqpTransport.TOPIC_EXCHANGE, getName());
      DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        long deliveryTag = delivery.getEnvelope().getDeliveryTag();
        Message message = new Message();
        message.merge(MessageCodec.decode(delivery.getBody()));
        message.setGroup(getName());
        try {
          listener.onMessage(message);
        } catch (Exception e) {
          Log.error("Message listener threw exception handling message " + deliveryTag + " on topic " + getName() + " - Reason: " + e.getLocalizedMessage());
        }
      };
      getChannel().basicConsume(queueName, true, deliverCallback, consumerTag -> {
      });
    } catch (IOException e) {
      Log.error("Problems attaching listener on topic " + getName() + " - Reason: " + e.getLocalizedMessage());
    }
  }


  @Override
  public void send(Message message) throws IOException {
    if (getChannel() != null) {
      getChannel().basicPublish(AmqpTransport.TOPIC_EXCHANGE, getName(), null, MessageCodec.encode(message));
    } else {
      throw new IOException("No channel set");
    }
  }


}
