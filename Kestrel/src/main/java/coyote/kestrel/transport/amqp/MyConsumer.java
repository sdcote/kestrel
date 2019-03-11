package coyote.kestrel.transport.amqp;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.*;
import coyote.loader.log.Log;

import java.io.IOException;

/**
 * This implementation supports recovery.
 */
public class MyConsumer extends DefaultConsumer implements Consumer {


  /**
   * Constructor.
   *
   * @param channel
   */
  public MyConsumer(Channel channel) {
    super(channel);
  }


  @Override
  public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) throws IOException {

    // Do what you have to do with your message.
    // Prefer a short processing...
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


  /**
   * @param consumerTag a consumer tag
   * @return a readable ID of this consumer
   */
  private String id(String consumerTag) {

    StringBuilder sb = new StringBuilder();
    sb.append(" Consumer tag = ");
    sb.append(consumerTag);
    sb.append(")");

    return sb.toString();
  }
}