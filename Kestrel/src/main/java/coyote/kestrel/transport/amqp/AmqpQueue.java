package coyote.kestrel.transport.amqp;

import com.rabbitmq.client.Channel;
import coyote.kestrel.transport.Message;
import coyote.kestrel.transport.MessageQueue;

import java.io.IOException;
import java.util.Map;

public class AmqpQueue extends AmqpChannel implements MessageQueue {

  public AmqpQueue(Channel channel, String name, boolean durable, boolean exclusive, boolean autodelete, Map<String, Object> arguments) {
    setChannel(channel);
    try {
      getChannel().queueDeclare(name, durable, exclusive, autodelete, arguments);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  @Override
  public Message getNextMessage() {
    return null;
  }

  @Override
  public Message getNextMessage(long timeout) {
    return null;
  }

  @Override
  public Message peek() {
    return null;
  }

  @Override
  public Message peek(long timeout) {
    return null;
  }
}
