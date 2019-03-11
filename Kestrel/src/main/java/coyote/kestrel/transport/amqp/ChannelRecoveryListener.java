package coyote.kestrel.transport.amqp;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Recoverable;
import com.rabbitmq.client.RecoveryListener;
import coyote.loader.log.Log;

public class ChannelRecoveryListener implements RecoveryListener {


  @Override
  public void handleRecovery(Recoverable recoverable) {
    if (recoverable instanceof Channel) {
      int channelNumber = ((Channel) recoverable).getChannelNumber();
      Log.warn("Connection to channel #" + channelNumber + " was recovered.");
    }
  }


  @Override
  public void handleRecoveryStarted(Recoverable recoverable) {
    Log.warn("Connection to channel #" + recoverable + " is recovering.");
  }

}