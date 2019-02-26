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
import java.util.concurrent.TimeoutException;

/**
 * This transport uses the RabbitMQ client to connect to AMPQ brokers.
 */
public class AmqpTransport implements Transport {

  //TODO: Use scoreboard for instrumentation
  //Scoreboad board = new Scoreboard(); maybe get it from the loader context?

  @Override
  public void setURI(URI uri) {

  }

  @Override
  public MessageChannel createInboxGroup() {
    return null;
  }



  @Override
  public void open() {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    factory.setUsername("guest");
    factory.setPassword("guest");
    factory.setConnectionTimeout(300000); //timeout in milliseconds; zero for infinite
    Connection connection = null;
    try {
      connection = factory.newConnection();
      Channel channel = connection.createChannel();



    } catch (IOException e) {
      e.printStackTrace();
    } catch (TimeoutException e) {
      e.printStackTrace();
    }

    //channel.queueDeclare(QUEUE_NAME, false, false, false, null);
  }

  @Override
  public void close() {

  }

  @Override
  public MessageQueue getQueue(String name) {
    return null;
  }

  @Override
  public MessageTopic getTopic(String name) {
    return null;
  }


}
