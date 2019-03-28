package coyote.kestrel.transport.amqp;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;
import coyote.kestrel.protocol.MessageCodec;
import coyote.kestrel.transport.Message;
import coyote.kestrel.transport.MessageListener;
import coyote.kestrel.transport.MessageQueue;
import coyote.loader.log.Log;

import java.io.IOException;
import java.util.Map;

import static coyote.kestrel.transport.amqp.AmqpTransport.DIRECT_EXCHANGE;

/**
 * All queues are direct; messages are routed based on the name of the queue
 * with multiple consumers allowed on a single named queue.
 */
public class AmqpQueue extends AmqpChannel implements MessageQueue {

  /**
   *
   * @param channel the channel on which to declare the queue
   * @param name the name of the queue
   * @param durable true if we are declaring a durable queue (the queue will survive a server restart)
   * @param exclusive true if we are declaring an exclusive queue (restricted to this connection)
   * @param autodelete true if we are declaring an autodelete queue (server will delete it when no longer in use)
   * @param arguments other properties (construction arguments) for the queue
   */
  public AmqpQueue(Channel channel, String name, boolean durable, boolean exclusive, boolean autodelete, Map<String, Object> arguments) {
    setChannel(channel);
    setName(name);
    try {
      AMQP.Queue.DeclareOk response = getChannel().queueDeclare(name, durable, exclusive, autodelete, arguments);
      getChannel().queueBind(name, DIRECT_EXCHANGE, name);
      Log.notice("Queue '" + response.getQueue() + "' declared with " + response.getMessageCount() + " messages waiting and " + response.getConsumerCount() + " consumers");
    } catch (IOException e) {
      Log.error(e);
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
        retval.merge(MessageCodec.decode(response.getBody()));
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
        Log.error(e);
      }
    }
  }


  @Override
  public void send(Message message) throws IOException {
    if (getChannel() != null) {
      getChannel().basicPublish(DIRECT_EXCHANGE, getName(), null, MessageCodec.encode(message));
    } else {
      throw new IOException("No channel set");
    }
  }

}
