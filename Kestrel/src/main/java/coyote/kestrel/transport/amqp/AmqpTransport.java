package coyote.kestrel.transport.amqp;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import coyote.kestrel.transport.MessageChannel;
import coyote.kestrel.transport.MessageQueue;
import coyote.kestrel.transport.MessageTopic;
import coyote.kestrel.transport.Transport;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * This transport uses the RabbitMQ client to connect to AMPQ brokers.
 */
public class AmqpTransport implements Transport {

  private static final boolean NON_DURABLE=false;
  private static final boolean DURABLE=true;
  private static final boolean EXCLUSIVE=false;
  private static final boolean NON_EXCLUSIVE=false;
  private static final boolean AUTO_DELETE=true;
  private static final boolean MANUAL_DELETE=false;
  private static final Map<String,Object> NO_ARGUMENTS=new HashMap<>();

  /** The connection abstracts the socket connection, and takes care of protocol version negotiation and authentication and so on for us. */
  private Connection connection = null;


  //TODO: Use scoreboard for instrumentation
  //Scoreboad board = new Scoreboard(); maybe get it from the loader context?

  @Override
  public void setURI(URI uri) {

  }

  @Override
  public MessageChannel createInboxChannel() {
    return null;
  }


  @Override
  public void open() {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    factory.setUsername("guest");
    factory.setPassword("guest");
    factory.setConnectionTimeout(300000); //timeout in milliseconds; zero for infinite
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

      retval = new AmqpQueue(connection.createChannel(),name, NON_DURABLE, NON_EXCLUSIVE, MANUAL_DELETE, NO_ARGUMENTS);



      // channel.queueDeclare("products_queue", false, false, false, null);
    } catch (IOException e) {
      e.printStackTrace();
    }


    return retval;
  }

  @Override
  public MessageTopic getTopic(String name) {

    // Create a FANOUT exchange or reuse an existing exchange. The name of the exchange is the same as the topic name
    // create a queue for this message channel to receive messages
    // bind the queue to exchange.

    return null;
  }


}
