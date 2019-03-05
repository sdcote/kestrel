package coyote.kestrel.transport;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MessageTest {


  @DisplayName("Basic message build")
  @Test
  void basicMessage() {

    Message message = new Message();
    message.setGroup("Service.Queue");
    message.setReplyGroup("My.Inbox");

    assertTrue("Service.Queue".equals(message.getGroup()));
    assertTrue("My.Inbox".equals(message.getReplyGroup()));
  }

}
