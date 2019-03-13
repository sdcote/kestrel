package coyote.profile;


import coyote.kestrel.proxy.ProxyBuilder;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ProxyBuilderTest {


  @DisplayName("Simple proxy build successful")
  @Ignore
  void testClientBuilder() {

    ProxyBuilder.addProxyClass(ProfileProxy.class);

    ProxyBuilder
            .setScheme("amqp")
            .setUsername("guest")
            .setPassword("guest")
            .setHost("localhost")
            .setPort(5672);

    ProfileClient client = ProxyBuilder.build(ProfileClient.class);
    assertNotNull(client);

    // client.retrieveProfile("123");
  }


}
