package coyote.kestrel.transport.amqp;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Recoverable;
import coyote.commons.StringUtil;
import coyote.kestrel.transport.Message;
import coyote.kestrel.transport.MessageQueue;
import coyote.kestrel.transport.MessageTopic;
import coyote.kestrel.transport.Transport;
import coyote.loader.log.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * This transport uses the RabbitMQ client to connect to AMPQ brokers.
 */
public class AmqpTransport implements Transport {

  public static final String DELIVERY_ID_FIELD = "AMQP_DLVRY_ID";
  static final String DIRECT_EXCHANGE = "DIRECT";
  static final String TOPIC_EXCHANGE = "TOPIC";
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
  private static final String DIRECT = "direct";
  private static final String TOPIC = "topic";
  private String hostname = DEFAULT_HOSTNAME;
  private int port = DEFAULT_PORT;
  private String username = DEFAULT_USERNAME;
  private String password = DEFAULT_PASSWORD;
  private int connectionTimeout = com.rabbitmq.client.ConnectionFactory.DEFAULT_CONNECTION_TIMEOUT;
  /**
   * The connection abstracts the socket connection, and takes care of protocol version negotiation and authentication and so on for us.
   */
  private Connection connection = null;
  /**
   * The channel this transport uses to send messages
   */
  private Channel outboundChannel = null;

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
  public MessageQueue createInbox() {
    AmqpQueue retval = null;
    try {
      String identifier = UUID.randomUUID().toString();
      Channel channel = connection.createChannel();
      channel.exchangeDeclare(DIRECT_EXCHANGE, DIRECT, DURABLE);
      retval = new AmqpQueue(connection.createChannel(), identifier, NON_DURABLE, EXCLUSIVE, AUTO_DELETE, NO_ARGUMENTS);
      ((Recoverable) channel).addRecoveryListener(retval);
    } catch (IOException e) {
      Log.error(e);
    }
    return retval;
  }


  @Override
  public void open() {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(getHostname());
    factory.setPort(getPort());
    factory.setUsername(getUsername());
    factory.setPassword(getPassword());
    factory.setConnectionTimeout(getConnectionTimeout());

    // Configure automatic reconnection
    factory.setAutomaticRecoveryEnabled(true);

    // Recovery interval: 10s
    factory.setNetworkRecoveryInterval(10000);

    // Exchanges and so on should be redeclared if necessary
    factory.setTopologyRecoveryEnabled(true);

    try {
      connection = factory.newConnection();
      outboundChannel = connection.createChannel();

    } catch (Exception e) {
      Log.fatal("Could not open AMPQ connection to broker(" + factory.getUsername() + "@" + factory.getHost() + ":" + factory.getPort() + "): " + e.getLocalizedMessage());
    }
  }


  @Override
  public void close() {
    // should we close each one of our channels first?
    try {
      connection.close();
    } catch (Exception ignore) {
      // ignore exceptions on close
    } finally {
      connection = null;
    }
  }


  @Override
  public MessageQueue getServiceQueue(String name) {
    AmqpQueue retval = null;
    if (connection != null) {
      try {
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(DIRECT_EXCHANGE, DIRECT, DURABLE);
        retval = new AmqpQueue(channel, name, DURABLE, NON_EXCLUSIVE, MANUAL_DELETE, NO_ARGUMENTS);
        ((Recoverable) channel).addRecoveryListener(retval);
      } catch (IOException e) {
        Log.error(e);
      }
    }
    return retval;
  }


  @Override
  public MessageTopic getTopic(String name) {
    AmqpTopic retval = null;
    try {
      Channel channel = connection.createChannel();
      channel.exchangeDeclare(TOPIC_EXCHANGE, TOPIC, DURABLE);
      retval = new AmqpTopic(channel, name);
      ((Recoverable) channel).addRecoveryListener(retval);
    } catch (IOException e) {
      Log.error(e);
    }
    return retval;
  }


  @Override
  public void sendDirect(Message msg) throws IOException {
    send(DIRECT_EXCHANGE, msg);
  }


  @Override
  public void broadcast(Message msg) throws IOException {
    send(TOPIC_EXCHANGE, msg);
  }


  private void send(String exchange, Message msg) throws IOException {
    if (outboundChannel != null) {
      if (StringUtil.isNotBlank(msg.getGroup())) {
        outboundChannel.basicPublish(exchange, msg.getGroup(), null, msg.getBytes());
      } else {
        throw new IOException("No message group name specified in message");
      }
    } else {
      throw new IOException("No outbound transport channel set");
    }
  }


}
