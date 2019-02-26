package coyote.kestrel.transport;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeoutException;

/**
 * This transport uses the RabbitMQ client to connect to AMPQ brokers.
 */
public class AmqpTransport implements Transport {


  @Override
  public void setURI(URI uri) {

  }

  @Override
  public String createInboxGroup() {
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
  public void attach(MessageConsumer consumer, String Group) {

  }

  @Override
  public void detach(MessageConsumer consumer, String Group) {

  }

  @Override
  public void attachSniffer(MessageConsumer consumer, String Group) {

  }

  @Override
  public void detachSniffer(MessageConsumer consumer, String Group) {

  }

  @Override
  public void send(Message msg) {

  }


}
