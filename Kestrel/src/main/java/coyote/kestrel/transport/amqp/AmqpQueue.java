package coyote.kestrel.transport.amqp;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;
import coyote.kestrel.protocol.PayloadCodec;
import coyote.kestrel.transport.Message;
import coyote.kestrel.transport.MessageListener;
import coyote.kestrel.transport.MessageQueue;
import coyote.loader.log.Log;

import java.io.IOException;
import java.util.Map;

/**
 * All queues are direct; messages are routed based on the name of the queue
 * with multiple consumers allowed on a single named queue.
 */
public class AmqpQueue extends AmqpChannel implements MessageQueue {

  public AmqpQueue(Channel channel, String name, boolean durable, boolean exclusive, boolean autodelete, Map<String, Object> arguments) {
    setChannel(channel);
    setName(name);
    try {
      AMQP.Queue.DeclareOk response = getChannel().queueDeclare(name, durable, exclusive, autodelete, arguments);
      Log.notice(response.getQueue() + " declared with " + response.getMessageCount() + " messages waiting and " + response.getConsumerCount() + " consumers");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  @Override
  public Message getNextMessage() {
    Message retval = null;
    try {
      GetResponse response = getChannel().basicGet(getName(), MANUAL_ACK);
      if (response != null) {
        AMQP.BasicProperties props = response.getProps();
        Log.debug(props);
        retval = new Message();
        retval.merge(PayloadCodec.decode(response.getBody()));
        retval.put(AmqpTransport.DELIVERY_ID_FIELD, response.getEnvelope().getDeliveryTag());
      }
    } catch (IOException e) {
      Log.error("Problems parsing message data: " + e.getMessage());
    }
    return retval;
  }


  @Override
  public Message peek() {
    return null;
  }

  @Override
  public Message peek(long timeout) {
    return null;
  }

  @Override
  public void attach(MessageListener listener) {
    if (listener != null) {
      SimpleConsumer consumer = new SimpleConsumer(getChannel());
      consumer.setListener(listener);
      consumer.setName(getName());

      try {
        getChannel().basicConsume(getName(), consumer);
      } catch (IOException e) {
        e.printStackTrace();
      }

    }
  }

  @Override
  public void detach(MessageListener listener) {

  }

  @Override
  public void send(Message message) throws IOException {
    if (getChannel() != null) {
      getChannel().basicPublish(AmqpTransport.DIRECT_EXCHANGE, getName(), null, message.getBytes());
    } else {
      throw new IOException("No channel set");
    }
  }
}
