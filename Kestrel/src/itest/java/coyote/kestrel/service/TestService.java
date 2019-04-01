package coyote.kestrel.service;

import coyote.kestrel.transport.Message;
import coyote.kestrel.transport.MessageListener;
import coyote.loader.cfg.ConfigurationException;

public class TestService extends AbstractService implements KestrelService, MessageListener {


  public static final String GROUP_NAME = "SVC.TEST";


  @Override
  public String getGroupName() {
    return GROUP_NAME;
  }

  @Override
  public void onConfiguration() throws ConfigurationException {

  }

  @Override
  public void process(Message message) {

  }

  @Override
  public void processInboxMessage(Message message) {

  }

  @Override
  public void processCoherenceMessage(Message message) {

  }

  @Override
  public void onShutdown() {

  }

  @Override
  public void onMessage(Message message) {

  }
}
