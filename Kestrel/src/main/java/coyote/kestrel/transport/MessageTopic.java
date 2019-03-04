package coyote.kestrel.transport;

public interface MessageTopic extends MessageChannel {


  /**
   * Subscribe to this channel.
   *
   * @param consumer
   */
  public void attach(MessageListener consumer);


  /**
   * Leave this channel.
   *
   * @param consumer
   */
  public void detach(MessageListener consumer);


}
