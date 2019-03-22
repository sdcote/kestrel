package coyote.kestrel.proxy;

import coyote.dataframe.DataFrame;
import coyote.kestrel.protocol.ResponseFuture;
import coyote.kestrel.transport.Message;

public class TestProxy extends AbstractProxy implements TestClient {


  @Override
  public void sendDirect(Message message) {
    Message request = createMessage(TestProtocol.GROUP_NAME);
    request.setPayload(new DataFrame().set("CMD", "Get").set("ID", "123"));
    ResponseFuture response = sendAndWait(request, 3000);
  }
}
