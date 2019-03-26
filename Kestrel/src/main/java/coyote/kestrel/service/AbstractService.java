package coyote.kestrel.service;

import coyote.commons.ExceptionUtil;
import coyote.commons.StringUtil;
import coyote.dataframe.DataFrame;
import coyote.i13n.Timer;
import coyote.kestrel.Kestrel;
import coyote.kestrel.protocol.KestrelProtocol;
import coyote.kestrel.protocol.MessageGroup;
import coyote.kestrel.transport.*;
import coyote.loader.AbstractLoader;
import coyote.loader.cfg.Config;
import coyote.loader.cfg.ConfigurationException;
import coyote.loader.log.Log;

import java.io.IOException;


/**
 * This is a base class for all services; handling much of the infrastructure.
 */
public abstract class AbstractService extends AbstractLoader implements KestrelService, MessageListener {

  private static final int DEFAULT_HEARTBEAT_INTERVAL = 60000;
  private static final String PROCESSING_TIMER = "processing";
  private static final String GROUP_NAME = "GroupName";
  private static final String SERVICE_RUNNING = "Running";
  private static final String SERVICE_TERMINATING = "Exiting";
  private static final String SERVICE_STARTED = "Started";

  /**
   * Sentinel flag for our main run loop.
   */
  private static volatile boolean running = true;
  /**
   * The message group we use to handle request/response protocol
   */
  protected MessageGroup serviceGroup = null;
  /**
   * The message group we use to coordinate our operations with other instances of this service
   */
  protected MessageTopic coherenceTopic = null;
  /**
   * The message group we use to handle Operations, Administration and Maintenance messages.
   */
  protected MessageQueue inbox = null;
  /**
   * The message broker connection
   */
  protected Transport transport = null;
  /**
   * The number of seconds between service heartbeats.
   */
  private int heartbeatInterval = DEFAULT_HEARTBEAT_INTERVAL;
  /**
   * Time of our last heartbeat
   */
  private volatile long lastHeartbeat = 0;


  /**
   * After the base class is configured and logging initialized, this method
   * is called to give loader a chance to initialize.
   *
   * @ see coyote.loader.AbstractLoader#onConfiguration()
   */
  @Override
  public void onConfiguration() throws ConfigurationException {
    // default no-op implementation
  }


  @Override
  public void onShutdown() {
    // default no-op implementation
  }


  /**
   * Override this method to handle messages sent directly to this instance
   * of the service.
   *
   * @param message the data received from the transport.
   */
  @Override
  public void processInboxMessage(Message message) {
    // default no-op implementation
  }


  /**
   * Override this method to handle coherence messages.
   *
   * <p>Coherence messages allow a group of services to coordinate processing.</p>
   *
   * @param message the data received from the transport.
   */
  @Override
  public void processCoherenceMessage(Message message) {
    // default no-op implementation
  }


  /**
   * Process messages which came in from some other channel.
   *
   * @param message the message sent to this consumer.
   */
  @Override
  public void processUncategorizedMessage(Message message) {
    // default no-op implementation
  }


  /**
   * Retrieve the name of the group on which the service listens for requests.
   *
   * @return a name of a group in which to listen for request messages.
   */
  public abstract String getGroupName();


  /**
   * Start the service running.
   *
   * <p>This is a blocking call; The thread is expected to remain in this
   * method until the loader terminates or an exception is thrown. Keep in
   * mind that some loaders will daemonize and this call will return
   * immediately. In such cases, the service will terminate when the JVM
   * terminates. Therefore, if this method spawns a thread then returns, the
   * JVM will remain running until that thread terminates.</p>
   */
  @Override
  public void start() {
    stats.setState(LOADER, SERVICE_STARTED);
    getStats().setVersion(Kestrel.PRODUCT_NAME, Kestrel.VERSION);
    Log.notice("Staring service on " + getGroupName());

    initializeMetrics();
    initializeMessageGroup();
    initializeInbox();
    initializeCoherence();

    if (transport.isValid()) {
      stats.setState(LOADER, SERVICE_RUNNING);
      while (running) {
        heartbeat();
        serviceGroupProcessing();
      }
      stats.setState(LOADER, SERVICE_TERMINATING);
      sendExitEvent();
    } else {
      Log.fatal("Could not connect to transport service");
    }
  }


  private void initializeMetrics() {
    // TODO: Make i13n configurable
    stats.enableArm(true);
    stats.enableGauges(true);
    stats.enableTiming(true);
  }


  /**
   * Pull the next message from the service group and process the message.
   *
   * <p>Message delivery is acknowledged which causes the message to be
   * removed from the queue. If an exception is thrown, the acknowledgement
   * is not sent and the message is re-queued for delivery.</p>
   */
  private void serviceGroupProcessing() {
    //Message message = serviceGroup.getNextMessage(10);
    Message message = getNextUnexpiredMessage();
    if (message != null) {
      try {
        Timer timer = stats.startTimer(PROCESSING_TIMER);
        process(message);
        timer.stop();
        serviceGroup.ackDelivery(message);
      } catch (final Exception e) {
        serviceGroup.nakDelivery(message);
        Log.error(ExceptionUtil.toString(e));
        if (Log.isLogging(Log.DEBUG_EVENTS)) {
          Log.debug(ExceptionUtil.stackTrace(e));
        }
      } // try-catch
    }
  }

  private Message getNextUnexpiredMessage() {
    Message retval = serviceGroup.getNextMessage(10);
    while (retval != null && retval.isExpired()) {
      if (Log.isLogging(Log.NOTICE_EVENTS)) Log.notice("Ignoring message (" + retval.getId() + ") - expired");
      retval = serviceGroup.getNextMessage(10);
    }
    return retval;
  }

  /**
   * Start listening to coherence messages
   */
  protected void initializeCoherence() {
    if (StringUtil.isNotBlank(getCoherenceGroupName())) {
      try {
        // create an inbox on which we will receive message directly to us
        coherenceTopic = getTransport().getTopic(getCoherenceGroupName());
        coherenceTopic.attach(this);
      } catch (Exception e) {
        running = false;
        Log.error("Could not initialize the coherence group");
      }
    }
  }

  /**
   * Services desiring to use coherence should override this method and return
   * the name of the topic to use for coherence communications.
   *
   * @return The name of the topic to use for coherence communications.
   */
  protected String getCoherenceGroupName() {
    return null;
  }


  protected void initializeMessageGroup() {
    try {
      // create a service message group using the standard Kestrel exchange protocol
      serviceGroup = new ServiceGroup(getGroupName());
      serviceGroup.setTransport(getTransport());
      serviceGroup.initialize();
    } catch (Exception e) {
      running = false;
      Log.error("Could not initialize the message group: " + e.getLocalizedMessage());
    }
  }


  private void initializeInbox() {
    try {
      inbox = getTransport().createInbox();
      stats.setId(inbox.getName()); // correlate this instance to our inbox
      inbox.attach(this);
    } catch (Exception e) {
      running = false;
      Log.error("Could not initialize the inbox");
    }
  }


  private Transport getTransport() {
    if (transport == null) {
      Config cfg = configuration.getSection(KestrelService.TRANSPORT_SECTION);
      if (cfg != null) {
        transport = createTransport(cfg);
      } else {
        Log.fatal("No transport configuration section found");
      }
    }
    return transport;
  }


  /**
   * Create a transport from the given configuration.
   *
   * <p>This method will never return null; it will an instance of an
   * InvalidTransport if the connection could not be opened for any reason.</p>
   *
   * @param cfg The configuration to use
   * @return An opened transport, or an InvalidTransport if the connection could not be made.
   */
  private Transport createTransport(Config cfg) {
    Transport retval = null;
    try {
      String transportUri = cfg.getString(KestrelService.URI_TAG, true);
      if (StringUtil.isNotEmpty(transportUri)) {
        retval = new TransportBuilder().setURI(transportUri).build();
        retval.open();
      } else {
        Log.fatal("No broker URI found in transport configuration, looking for '" + KestrelService.URI_TAG + "'");
        retval = new InvalidTransport();
      }
    } catch (Exception e) {
      Log.error("Could not build transport: " + e.getLocalizedMessage());
      retval = new InvalidTransport();
    }
    return retval;
  }


  /**
   * This is where inbox and coherence messages arrive for processing
   *
   * @param message The message to receive and process.
   */
  @Override
  public void onMessage(Message message) {
    if (message != null) {
      if (inbox.getName().equals(message.getGroup())) {
        processInboxMessage(message);
      } else if (StringUtil.isNotBlank(getCoherenceGroupName()) && getCoherenceGroupName().equals(message.getGroup())) {
        processCoherenceMessage(message);
      } else {
        processUncategorizedMessage(message);
      }
      // What if this comes in with an unexpected group name?
    }
  }


  /**
   * Send a message across the transport.
   *
   * <p>The message group set inside the message will determine how the the
   * message will be routed.</p>
   *
   * @param message The message to send
   * @throws IOException if problems were encountered sending the message.
   */
  protected void send(Message message) throws IOException {
    getTransport().sendDirect(message);
  }

  /**
   * Broadcast a message across the transport.
   *
   * <p>The message group set inside the message will determine how the the
   * message will be routed.</p>
   *
   * @param message The message to send
   * @throws IOException if problems were encountered sending the message.
   */
  protected void broadcast(Message message) throws IOException {
    getTransport().broadcast(message);
  }

  /**
   * Acknowledge the message with the given payload.
   *
   * <p>This is not a transport level protocol message. It is a response to
   * the Kestrel request/response protocol.</p>
   *
   * @param requestMessage  The message used to generate the response.
   * @param responsePayload The payload to send in the response.
   */
  protected void sendAck(Message requestMessage, DataFrame responsePayload) {
    sendResponse(KestrelProtocol.ACK_TYPE, requestMessage, responsePayload, null, -1);
  }

  /**
   * Acknowledge the message with the given text.
   *
   * <p>This is not a transport level protocol message. It is a response to
   * the Kestrel request/response protocol.</p>
   *
   * @param requestMessage The message used to generate the response.
   * @param message        The text message to include in the response.
   */
  protected void sendAck(Message requestMessage, String message) {
    sendResponse(KestrelProtocol.ACK_TYPE, requestMessage, null, message, -1);
  }

  /**
   * Acknowledge the message with the given text and result code.
   *
   * <p>This is not a transport level protocol message. It is a response to
   * the Kestrel request/response protocol.</p>
   *
   * @param requestMessage The message used to generate the response.
   * @param message        Text message to include in the response.
   * @param resultCode     The numeric result code.
   */
  protected void sendAck(Message requestMessage, String message, int resultCode) {
    sendResponse(KestrelProtocol.ACK_TYPE, requestMessage, null, message, resultCode);
  }

  /**
   * Acknowledge the message with the given result code.
   *
   * <p>This is not a transport level protocol message. It is a response to
   * the Kestrel request/response protocol.</p>
   *
   * @param requestMessage The message used to generate the response.
   * @param resultCode     The numeric result code.
   */
  protected void sendAck(Message requestMessage, int resultCode) {
    sendResponse(KestrelProtocol.ACK_TYPE, requestMessage, null, null, resultCode);
  }

  /**
   * Send a negative acknowledgement in response to the given request message.
   *
   * <p>This is not a transport level protocol message. It is a response to
   * the Kestrel request/response protocol.</p>
   *
   * @param requestMessage The message used to generate the response.
   * @param message        Text message to include in the response.
   */
  protected void sendNak(Message requestMessage, String message) {
    sendResponse(KestrelProtocol.NAK_TYPE, requestMessage, null, message, -1);
  }

  /**
   * Send a NAK response to the given message with message text and a result code.
   *
   * <p>This is not a transport level protocol message. It is a response to
   * the Kestrel request/response protocol.</p>
   *
   * @param requestMessage The message used to generate the response.
   * @param message        Text message to include in the response.
   * @param resultCode     The numeric result code.
   */
  protected void sendNak(Message requestMessage, String message, int resultCode) {
    sendResponse(KestrelProtocol.NAK_TYPE, requestMessage, null, message, resultCode);
  }

  /**
   * Send a NAK response to the given message with a result code.
   *
   * <p>This is not a transport level protocol message. It is a response to
   * the Kestrel request/response protocol.</p>
   *
   * @param requestMessage The message used to generate the response.
   * @param resultCode     The numeric result code.
   */
  protected void sendNak(Message requestMessage, int resultCode) {
    sendResponse(KestrelProtocol.NAK_TYPE, requestMessage, null, null, resultCode);
  }

  /**
   * This generates a response message from the given data and sends it across
   * the current message transport.
   *
   * @param type            The type (e.g. ACK, NAK) of message to send
   * @param requestMessage  The message used to generate the response.
   * @param responsePayload The optional payload for the message
   * @param message         The optional text message to include in the response
   * @param resultCode      The optional result code for the response
   */
  private void sendResponse(String type, Message requestMessage, DataFrame responsePayload, String message, int resultCode) {
    Message response = KestrelProtocol.createResponse(requestMessage);
    response.setType(type);
    if (responsePayload != null) response.setPayload(responsePayload);
    if (message != null) response.setMessage(message);
    if (resultCode > 0) response.setResultCode(resultCode);
    try {
      send(response);
    } catch (IOException e) {
      Log.error("Could not send '" + type + "' response: " + e.getMessage());
    }
  }


  protected void heartbeat() {
    long now = System.currentTimeMillis();
    if (lastHeartbeat + heartbeatInterval <= now) {
      DataFrame payload = StatUtil.createStatusFrame(stats);
      payload.set(GROUP_NAME, getGroupName());

      Message heartbeat = new Message();
      heartbeat.setType(KestrelProtocol.HEARTBEAT_TYPE);
      heartbeat.setPayload(payload);
      heartbeat.setGroup(KestrelProtocol.HEARTBEAT_GROUP);

      try {
        broadcast(heartbeat);
      } catch (IOException e) {
        Log.error("Could not send heartbeat message: " + e.getLocalizedMessage());
      }

      lastHeartbeat = now;
    }
  }

  /**
   * Send a final heartbeat event on the OAM channel
   */
  private void sendExitEvent() {
    Message heartbeat = new Message();
    heartbeat.setType(KestrelProtocol.HEARTBEAT_TYPE);
    heartbeat.setGroup(KestrelProtocol.HEARTBEAT_GROUP);
    DataFrame payload = new DataFrame().set("Event", "Terminating").set("InstanceId", inbox.getName());
    heartbeat.setPayload(payload);
    try {
      broadcast(heartbeat);
    } catch (IOException e) {
      Log.error("Could not send final heartbeat event message: " + e.getLocalizedMessage());
    }
    Log.notice("Exit event for " + inbox.getName());
  }


  /**
   * Shut everything down when the JRE terminates.
   *
   * <p>There is a shutdown hook registered with the JRE when this Job is
   * loaded. The shutdown hook will call this method when the JRE is
   * terminating so that the service can terminate any long-running processes.</p>
   *
   * <p>Note: this is different from {@code close()} but {@code shutdown()}
   * will normally result in {@code close()} being invoked at some point.</p>
   */
  @Override
  public void shutdown() {
    running = false;
    closeTransport();
    try {
      onShutdown();
    } catch (Throwable ball) {
      Log.warn(ball);
    }
  }


  private void closeTransport() {
    if (transport != null && transport.isValid()) {
      transport.close();
    }
  }

}

