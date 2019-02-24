package coyote.kestrel.protocol;

import coyote.kestrel.transport.Message;
import coyote.kestrel.transport.Transport;

/**
 * Message groups represent the concept of a grouping of messages exchanged
 * between components using some protocol.
 *
 * <p>Each group represents an exchange pattern, request/reply or event, which
 * allows components to interact in an orderly manner</p>
 */
public interface MessageGroup {

  void respond(Message response);

  ResponseFuture request(Message request);

  void send(Message message);

  void setTransport(Transport transport);

  void setGroup(String groupName);

  void initialize();

}
