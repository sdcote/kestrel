package coyote.kestrel.transport.amqp;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.*;
import coyote.kestrel.protocol.PayloadCodec;
import coyote.kestrel.transport.Message;
import coyote.kestrel.transport.MessageListener;
import coyote.loader.log.Log;

import java.io.IOException;

/**
 * This consumer implementation supports recovery.
 */
public class SimpleConsumer extends DefaultConsumer implements Consumer {
  private MessageListener listener = null;
  private String name = null;


  public SimpleConsumer(Channel channel) {
    super(channel);
  }


  public String getName() {
    return name;
  }


  public void setName(String name) {
    this.name = name;
  }


  @Override
  public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) throws IOException {
    long deliveryTag = envelope.getDeliveryTag();

    if (listener != null) {
      Message message = new Message();
      message.merge(PayloadCodec.decode(body));
      message.setGroup(getName());
      try {
        listener.onMessage(message);
        getChannel().basicAck(deliveryTag, false);
      } catch (Exception e) {
        getChannel().basicNack(deliveryTag,false,true);
      }
    } else{
      Log.warn("Consumer on '"+getName()+"' has no listener - Requeueing");
      getChannel().basicNack(deliveryTag,false,true);
    }
  }


  @Override
  public void handleShutdownSignal(String consumerTag, ShutdownSignalException signal) {
    if (signal.isInitiatedByApplication()) {
      Log.warn("The connection to the messaging server was shut down." + id(consumerTag));

    } else if (signal.getReference() instanceof Channel) {
      int nb = ((Channel) signal.getReference()).getChannelNumber();
      Log.warn("A RabbitMQ consumer was shut down. Channel #" + nb + ", " + id(consumerTag));

    } else {
      Log.warn("A RabbitMQ consumer was shut down." + id(consumerTag));
    }
  }


  @Override
  public void handleCancelOk(String consumerTag) {
    Log.warn("A RabbitMQ consumer stops listening to new messages." + id(consumerTag));
  }


  @Override
  public void handleCancel(String consumerTag) throws IOException {
    Log.warn("A RabbitMQ consumer UNEXPECTEDLY stops listening to new messages." + id(consumerTag));
  }


  private String id(String consumerTag) {
    StringBuilder sb = new StringBuilder();
    sb.append(" Consumer tag = ");
    sb.append(consumerTag);
    sb.append(")");
    return sb.toString();
  }


  public void setListener(MessageListener listener) {
    this.listener = listener;
  }

}