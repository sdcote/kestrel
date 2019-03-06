package coyote.kestrel.transport;

import coyote.dataframe.DataFrame;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
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

  @DisplayName("Basic message payload")
  @Test
  void basicPayload() {

    Message message = new Message();
    assertTrue(message.getFieldCount() == 0, "Message should have no fields when new");

    DataFrame payload = message.getPayload();
    assertNotNull(payload);
    assertTrue(message.getFieldCount() == 0, "Message should have no fields when new payload is retrieved");
    message.setPayload(payload);
    assertTrue(message.getFieldCount() == 1, "Message should have one field when payload is set");

  }

}
