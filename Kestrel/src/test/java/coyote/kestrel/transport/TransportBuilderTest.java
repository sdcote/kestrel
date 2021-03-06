package coyote.kestrel.transport;


import coyote.commons.StringUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TransportBuilderTest {


  @DisplayName("Basic URI configuration")
  @Test
  void basicUri() throws URISyntaxException {
    URI uri = new URI("amqp://user:passwd@localhost:5672");
    TransportBuilder builder = new TransportBuilder().setURI(uri);

    assertTrue(StringUtil.isNotEmpty(builder.getScheme()));
    assertTrue(StringUtil.isNotEmpty(builder.getUsername()));
    assertTrue(StringUtil.isNotEmpty(builder.getPassword()));
    assertTrue(StringUtil.isNotEmpty(builder.getHostname()));
    assertTrue(builder.getPort() == 5672);
  }

  @DisplayName("String URI configuration")
  @Test
  void stringUri() {
    TransportBuilder builder = new TransportBuilder().setURI("amqp://user:passwd@localhost:5672");

    assertTrue(StringUtil.isNotEmpty(builder.getScheme()));
    assertTrue(StringUtil.isNotEmpty(builder.getUsername()));
    assertTrue(StringUtil.isNotEmpty(builder.getPassword()));
    assertTrue(StringUtil.isNotEmpty(builder.getHostname()));
    assertTrue(builder.getPort() == 5672);
  }

  @DisplayName("Basic builder configuration")
  @Test
  void basicBuilder() {
    TransportBuilder builder = new TransportBuilder()
            .setScheme("amqp")
            .setUsername("guest")
            .setPassword("guest")
            .setHost("localhost")
            .setPort(5672)
            .setConnectionTimeout(500)
            .setPath("/vhost");

    assertTrue(StringUtil.isNotEmpty(builder.getScheme()));
    assertTrue(StringUtil.isNotEmpty(builder.getUsername()));
    assertTrue(StringUtil.isNotEmpty(builder.getPassword()));
    assertTrue(StringUtil.isNotEmpty(builder.getHostname()));
    assertTrue(builder.getPort() == 5672);
    assertTrue(StringUtil.isNotEmpty(builder.getPath()));
    assertTrue(builder.getConnectionTimeout() == 500);

  }

  @DisplayName("String URI configuration")
  @Test
  void addFailover() {
    TransportBuilder builder = new TransportBuilder().setURI("amqp://guest:guest@localhost:5672");
    builder.addFailover("amqp://guest:guest@127.0.0.1:5672");
    builder.addFailover("amqp://guest:guest@192.168.10.20:5672");

  }
}
