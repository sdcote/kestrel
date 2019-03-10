package coyote.kestrel.transport.amqp;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import coyote.kestrel.transport.Inbox;
import coyote.kestrel.transport.MessageQueue;
import coyote.kestrel.transport.MessageTopic;
import coyote.kestrel.transport.Transport;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;


/**
 * This transport uses the RabbitMQ client to connect to AMPQ brokers.
 */
public class AmqpTransport implements Transport {

    private static final boolean NON_DURABLE = false;
    private static final boolean DURABLE = true;
    private static final boolean EXCLUSIVE = false;
    private static final boolean NON_EXCLUSIVE = false;
    private static final boolean AUTO_DELETE = true;
    private static final boolean MANUAL_DELETE = false;
    private static final Map<String, Object> NO_ARGUMENTS = new HashMap<>();
    private static final String DEFAULT_HOSTNAME = "localhost";
    private static final int DEFAULT_PORT = 5672;
    private static final String DEFAULT_USERNAME = "guest";
    private static final String DEFAULT_PASSWORD = "guest";
    private String hostname = DEFAULT_HOSTNAME;
    private int port = DEFAULT_PORT;
    private String username = DEFAULT_USERNAME;
    private String password = DEFAULT_PASSWORD;
    private int connectionTimeout = com.rabbitmq.client.ConnectionFactory.DEFAULT_CONNECTION_TIMEOUT;
    /**
     * The connection abstracts the socket connection, and takes care of protocol version negotiation and authentication and so on for us.
     */
    private Connection connection = null;
    private String virtualHost = null;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * timeout in milliseconds; zero for infinite
     */
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getVirtualHost() {
        return virtualHost;
    }

    public void setVirtualHost(String virtualHoat) {
        this.virtualHost = virtualHoat;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public Inbox createInboxChannel() {
        return null;
    }


    @Override
    public void open() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(getHostname());
        factory.setUsername(getUsername());
        factory.setPassword(getPassword());
        factory.setConnectionTimeout(getConnectionTimeout());
        try {
            connection = factory.newConnection();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        // should we close each one of our channels first?
        try {
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connection = null;
        }
    }

    @Override
    public MessageQueue getQueue(String name) {
        // look in the cache to see if there is already a queue with that name
        AmqpQueue retval = null;
        try {
            retval = new AmqpQueue(connection.createChannel(), name, NON_DURABLE, NON_EXCLUSIVE, MANUAL_DELETE, NO_ARGUMENTS);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return retval;
    }

    @Override
    public MessageTopic getTopic(String name) {

        // Create a TOPIC exchange or reuse an existing exchange. The name of the exchange is the same as the topic name
        AmqpTopic retval = new AmqpTopic();
        retval.setName(name);
        try {
            retval.setChannel(connection.createChannel());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return retval;
    }


}
