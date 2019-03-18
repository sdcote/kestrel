package coyote.kestrel.transport;


import java.io.IOException;

/**
 * Channels are how messages are sent and received.
 *
 * <p>Subclasses of MessageChannel determines qualities of service (QoS);
 * Queue or Topic, durable, persistent, etc. QoS concepts will be
 * simplified across transports to prevent vendor-specific constructs.</p>
 */
public interface MessageChannel {


  String getName();

  void setName(String name);



  /**
   * Join to this channel.
   *
   * @param listener the message listener to receive messages from this channel
   */
  void attach(MessageListener listener);


  /**
   * Leave this channel.
   *
   * @param listener the message listener to receive messages from this channel
   */
  void detach(MessageListener listener);

  void ackDelivery(Message message);

  void nakDelivery(Message message);


}
