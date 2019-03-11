package coyote.kestrel.transport.amqp;

import com.rabbitmq.client.Channel;
import coyote.kestrel.transport.Message;
import coyote.kestrel.transport.MessageChannel;

import java.io.IOException;


/**
 * Channels contain a reference to the rabbit channel to help keep channel
 * operations limited to a single thread. A worker thread can own this
 * reference and be the only one performing AMQP channel operations.
 */
public abstract class AmqpChannel implements MessageChannel {

    private Channel channel = null;
    private String name = null;

    protected static final boolean AUTO_ACK = true;
    protected static final boolean MANUAL_ACK = false;


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


    /**
     * @param msg
     * @throws IOException if the message could not be sent
     */
    @Override
    public void send(Message msg) throws IOException {
        if (channel != null) {
            channel.basicPublish("", getName(), null, msg.getBytes());
        } else {
            throw new IOException("No transport channel set");
        }
    }
}
