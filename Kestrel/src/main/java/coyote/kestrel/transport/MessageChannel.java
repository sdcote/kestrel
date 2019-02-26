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


  /**
   * Subscribe to this channel.
   *
   * @param consumer
   */
  public void attach(MessageConsumer consumer);


  /**
   * Leave this channel.
   *
   * @param consumer
   */
  public void detach(MessageConsumer consumer);


  /**
   * Attach a sniffer to this channel.
   *
   * <p>Sniffers peek at messages but do not consume them from the broker;
   * they are still available for the attached MessageConsumers.</p>
   *
   * <p>Use only during debugging, as this can degrade performance.</p>
   *
   * @param consumer
   */
  public void attachSniffer(MessageConsumer consumer);


  /**
   * Remove a sniffer from this channel.
   *
   * @param consumer
   */
  public void detachSniffer(MessageConsumer consumer);

}
