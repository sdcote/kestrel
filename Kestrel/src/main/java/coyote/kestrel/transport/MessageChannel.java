package coyote.kestrel.transport;


/**
 * Channels are how messages are sent and received.
 *
 * <p>Subclasses of MessageChannel determines qualities of service (QoS);
 * Queue or Topic, durable, persistent, etc. QoS concepts will be
 * simplified across transports to prevent vendor-specific constructs.</p>
 */
public interface MessageChannel {


  /**
   * Send the message on this channel.
   *
   * @param msg
   */
  void send(Message msg);





}
