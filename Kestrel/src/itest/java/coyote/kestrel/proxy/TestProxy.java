package coyote.kestrel.proxy;

import coyote.dataframe.DataFrame;
import coyote.kestrel.transport.Message;

import java.io.IOException;

public class TestProxy extends AbstractProxy implements TestClient {


  @Override
  public void sendDirect(Message message) throws IOException {
    Message request = createMessage(TestProtocol.GROUP_NAME);
    request.setPayload(new DataFrame().set("CMD", "Get").set("ID", "123"));
    getTransport().sendDirect(request);
  }

  @Override
  public void sendMany(String groupName,int count) throws IOException {
    Message request = createMessage(groupName);
    request.setPayload(new DataFrame().set("CMD", "Get").set("ID", "123"));

    for(int x =0; x<count;x++){
      send(request);
    }

  }
}
