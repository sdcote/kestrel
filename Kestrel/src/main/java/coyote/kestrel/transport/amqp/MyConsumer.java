package coyote.kestrel.transport.amqp;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.*;
import coyote.dataframe.DataFrame;
import coyote.dataframe.DecodeException;
import coyote.dataframe.marshal.json.JsonFrameParser;
import coyote.kestrel.transport.Message;
import coyote.kestrel.transport.MessageListener;
import coyote.loader.log.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * This implementation supports recovery.
 */
public class MyConsumer extends DefaultConsumer implements Consumer {
  private MessageListener listener = null;
  private String name = null;

  /**
   * Constructor.
   *
   * @param channel
   */
  public MyConsumer(Channel channel) {
    super(channel);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) throws IOException {
    long deliveryTag = envelope.getDeliveryTag();

    if (listener != null) {
      DataFrame payload = null;

      Message message = new Message();
      message.setGroup(getName());

      try {
        payload = new DataFrame(body);
      } catch (DecodeException e) {
        String data = StandardCharsets.ISO_8859_1.decode(ByteBuffer.wrap(body)).toString();
        try {
          List<DataFrame> frames = new JsonFrameParser(data).parse();
          if (frames.size() > 0) {
            if (frames.size() == 1) {
              payload = frames.get(0);
            } else {
              payload = new DataFrame();
              for (DataFrame frame : frames) {
                payload.add(frame);
              }
            }
          } else {
            payload = new DataFrame().set("MSG", data);
          }
        } catch (Throwable ball) {
          payload = new DataFrame().set("MSG", data); // unknown string
        }
      } catch (Throwable ball) {
        payload = new DataFrame().set("MSG", body); // unknown binary
      }

      if (payload != null) {
        message.setPayload(payload);
      }

      // deliver to the message listener
      listener.onMessage(message);
    }
    getChannel().basicAck(deliveryTag, true);
  }


  @Override
  public void handleShutdownSignal(String consumerTag, ShutdownSignalException signal) {

    if (signal.isInitiatedByApplication()) {
      Log.warn("The connection to the messaging server was shut down." + id(consumerTag));

    } else if (signal.getReference() instanceof Channel) {
      int nb = ((Channel) signal.getReference()).getChannelNumber();
      Log.warn("A RabbitMQ consumer was shut down. Channel #" + nb + ", " + id(consumerTag));

    } else {
      Log.warn("A RabbitMQ consumer was shut down." + id(consumerTag));
    }
  }


  @Override
  public void handleCancelOk(String consumerTag) {
    Log.warn("A RabbitMQ consumer stops listening to new messages." + id(consumerTag));
  }


  @Override
  public void handleCancel(String consumerTag) throws IOException {
    Log.warn("A RabbitMQ consumer UNEXPECTEDLY stops listening to new messages." + id(consumerTag));
  }


  /**
   * @param consumerTag a consumer tag
   * @return a readable ID of this consumer
   */
  private String id(String consumerTag) {

    StringBuilder sb = new StringBuilder();
    sb.append(" Consumer tag = ");
    sb.append(consumerTag);
    sb.append(")");

    return sb.toString();
  }

  public void setListener(MessageListener listener) {
    this.listener = listener;
  }
}