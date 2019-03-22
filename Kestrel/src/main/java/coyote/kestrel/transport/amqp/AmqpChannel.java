package coyote.kestrel.transport.amqp;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Recoverable;
import com.rabbitmq.client.RecoveryListener;
import coyote.dataframe.DataFrameException;
import coyote.kestrel.transport.Message;
import coyote.kestrel.transport.MessageChannel;
import coyote.loader.log.Log;

import java.io.IOException;


/**
 * Channels contain a reference to the rabbit channel to help keep channel
 * operations limited to a single thread. A worker thread can own this
 * reference and be the only one performing AMQP channel operations.
 */
public abstract class AmqpChannel implements MessageChannel, RecoveryListener {

  protected static final boolean AUTO_ACK = true;
  protected static final boolean MANUAL_ACK = false;
  private Channel channel = null;
  private String name = null;

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  public Channel getChannel() {
    return channel;
  }

  public void setChannel(Channel channel) {
    this.channel = channel;
  }


  @Override
  public void ackDelivery(Message message) {
    try {
      long deliveryId = message.getAsLong(AmqpTransport.DELIVERY_ID_FIELD);
      getChannel().basicAck(deliveryId, false);
    } catch (DataFrameException e) {
      Log.error("Could not ACK delivery, could not retrieve delivery identifier from message: " + e.getLocalizedMessage());
    } catch (IOException e) {
      Log.error("Could not ACK delivery, channel error: " + e.getLocalizedMessage());
    }
  }


  @Override
  public void nakDelivery(Message message) {
    try {
      long deliveryId = message.getAsLong(AmqpTransport.DELIVERY_ID_FIELD);
      getChannel().basicNack(deliveryId, false, true);
    } catch (DataFrameException e) {
      Log.error("Could not NCK delivery, could not retrieve delivery identifier from message: " + e.getLocalizedMessage());
    } catch (IOException e) {
      Log.error("Could not NCK delivery, channel error: " + e.getLocalizedMessage());
    }
  }


  @Override
  public void handleRecovery(Recoverable recoverable) {
    if (recoverable instanceof Channel) {
      int channelNumber = ((Channel) recoverable).getChannelNumber();
      Log.warn("Connection to channel #" + channelNumber + " was recovered.");
    }
  }


  @Override
  public void handleRecoveryStarted(Recoverable recoverable) {
    Log.warn("Connection to channel #" + recoverable + " is recovering.");
  }

}
