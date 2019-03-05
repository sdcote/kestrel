package coyote.kestrel.transport.amqp;

import com.rabbitmq.client.Channel;
import coyote.kestrel.transport.MessageListener;
import coyote.kestrel.transport.MessageTopic;

/**
 * AMQP messages are sent to an exchange type of "Topic" and the routing key is used to control which queues receive messages.
 */
public class AmqpTopic extends AmqpChannel implements MessageTopic {


    @Override
  public void attach(MessageListener consumer) {

  }

  @Override
  public void detach(MessageListener consumer) {

  }


}
