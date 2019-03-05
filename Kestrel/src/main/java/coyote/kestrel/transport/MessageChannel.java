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
     * Send the message on this channel.
     *
     * @param msg
     * @throws java.io.IOException if an error is encountered
     */
    void send(Message msg) throws IOException;


}
