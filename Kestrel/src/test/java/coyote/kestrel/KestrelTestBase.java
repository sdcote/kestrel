package coyote.kestrel;

import coyote.kestrel.transport.Transport;
import coyote.kestrel.transport.test.TestTransport;

public class KestrelTestBase {

  protected Transport createTestTransport() {
    Transport retval = null;
    // retval = new TransportBuilder().setURI("amqp://guest:guest@localhost:5672").build();
    retval = new TestTransport();
    return retval;
  }

}
