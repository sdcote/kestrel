package coyote.kestrel.proxy;

import coyote.kestrel.transport.Message;

import java.io.IOException;

public interface TestClient extends KestrelProxy {
  void sendDirect(Message message) throws IOException;

  void sendMany(String groupName, int count) throws IOException;
}
