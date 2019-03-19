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


  /**
   * @return the name of this channel
   */
  String getName();


  /**
   * Set the name of the channel.
   *
   * @param name the name of the channel to set.
   */
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


  /**
   * Send the message on this channel.
   *
   * @param message the message to send
   * @throws IOException if any problems were encountered sending the message
   */
  void send(Message message) throws IOException;

}
