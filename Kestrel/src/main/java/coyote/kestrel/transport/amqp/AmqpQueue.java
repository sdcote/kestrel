package coyote.kestrel.transport.amqp;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;
import coyote.dataframe.DataFrame;
import coyote.kestrel.KestrelProtocol;
import coyote.kestrel.transport.Message;
import coyote.kestrel.transport.MessageQueue;
import coyote.loader.log.Log;

import java.io.IOException;
import java.util.Map;

/**
 * All queues are direct; messages are routed based on the name of the queue
 * with multiple consumers allowed on a single named queue.
 */
public class AmqpQueue extends AmqpChannel implements MessageQueue {
  private static final String EXCHANGE_NAME = "DIRECT";

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
      GetResponse response = getChannel().basicGet(getName(), AUTO_ACK);
      if (response != null) {
        AMQP.BasicProperties props = response.getProps();
        Log.notice(props);
        retval = new Message();
        retval.put(KestrelProtocol.ID_FIELD, response.getEnvelope().getDeliveryTag());
        byte[] body = response.getBody();
        DataFrame payload = null;
        try {
          payload = new DataFrame(body);
        } catch (Throwable ball) {
          payload = new DataFrame();
          payload.put("BYTES", body);
          payload.put("ERROR", ball.getLocalizedMessage());
        }
        retval.setPayload(payload);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return retval;
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
