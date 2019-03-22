package coyote.kestrel.proxy;

import coyote.kestrel.transport.Message;

public interface TestClient extends KestrelProxy {
  void sendDirect(Message message);
}
