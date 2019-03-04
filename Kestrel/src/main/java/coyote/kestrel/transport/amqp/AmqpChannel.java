package coyote.kestrel.transport.amqp;

import com.rabbitmq.client.Channel;
import coyote.kestrel.transport.Message;
import coyote.kestrel.transport.MessageChannel;


/**
 * Channels contain a reference to the rabbit channel to help keep channel
 * operations limited to a single thread. A worker thread can own this
 * reference and be the only one performing AMQP channel operations.
 */
public class AmqpChannel implements MessageChannel {

  public Channel getChannel() {
    return channel;
  }

  public void setChannel(Channel channel) {
    this.channel = channel;
  }

  private Channel channel = null;

  @Override
  public void send(Message msg) {

  }

}
