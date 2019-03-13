package coyote.kestrel.service;

import coyote.commons.ExceptionUtil;
import coyote.commons.StringUtil;
import coyote.kestrel.ServiceGroup;
import coyote.kestrel.protocol.MessageGroup;
import coyote.kestrel.transport.*;
import coyote.loader.AbstractLoader;
import coyote.loader.cfg.Config;
import coyote.loader.cfg.ConfigurationException;
import coyote.loader.log.Log;


/**
 * This is a base class for all services; handling much of the infrastructure.
 */
public abstract class AbstractService extends AbstractLoader implements KestrelService, MessageListener {

  private static final int DEFAULT_HEARTBEAT_INTERVAL = 300;
  private static volatile boolean running = true;
  /**
   * The message group we use to handle request/response protocol
   */
  protected MessageGroup serviceGroup = null;
  /**
   * The message group we use to coordinate our operations with other instances of this service
   */
  protected MessageGroup coherenceGroup = null;
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


  @Override
  public void processInboxMessage(Message message) {
    // default no-op implementation
  }

  @Override
  public void processCoherenceMessage(Message message) {
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
    Log.notice("Staring service on " + getGroupName());

    initializeMessageGroup();
    initializeInbox();
    initializeCoherence();

    if (transport.isValid()) {
      while (running) {
        heartbeat();
        serviceGroupProcessing();
      }
    } else {
      Log.fatal("Could not connect to transport service");
    }
  }

  private void serviceGroupProcessing() {
    Message message = serviceGroup.getNextMessage(100);
    if (message != null) {
      try {
        process(message);
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


  private void coherenceProcessing() {
    Message message = inbox.getNextMessage();
    while (message != null) {
      try {
        processCoherenceMessage(message);
      } catch (final Exception e) {
        Log.error(ExceptionUtil.toString(e));
        Log.debug(ExceptionUtil.stackTrace(e));
      } // try-catch
      message = inbox.getNextMessage();
    }
  }


  protected void inboxProcessing() {
    // Pull a message from our inbox to see if we have an OAM message to process
    Message oam = inbox.getNextMessage();
    while (oam != null) {
      try {
        processInboxMessage(oam);
      } catch (final Exception e) {
        Log.error(ExceptionUtil.toString(e));
        Log.debug(ExceptionUtil.stackTrace(e));
      } // try-catch
      oam = inbox.getNextMessage();
    }
  }

  protected void initializeCoherence() {

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
      // create an inbox on which we will receive message directly to us
      inbox = getTransport().createInbox();
      // start receiving messages and send them to this listener
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
   * @return an opened transport, or an InvalidTransport if the connection could not be made.
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
      } else {
        // TODO: check for coherence channel
        processCoherenceMessage(message);
      }
      // What if this comes in with an unexpected group name?
    }
  }

  protected void respond(Message response) {
    serviceGroup.respond(response);
  }

  protected void send(Message response) {
    serviceGroup.send(response);
  }

  /**
   * Send a Kestrel NAK; a NAK to the client. This does not NAK message
   * delivery at the transport layer.
   *
   * <p>This indicates the message could not be processed by the service.</p>
   *
   * @param message
   * @param msg
   */
  protected void sendNak(Message message, String msg) {
  }

  /**
   * Send a Kestrel NAK; a NAK to the client. This does not NAK message
   * delivery at the transport layer.
   *
   * <p>This indicates the message could not be processed by the service.</p>
   *
   * @param message
   * @param resultcode
   */
  protected void sendNak(Message message, int resultcode) {
  }


  protected void heartbeat() {
    // check heartbeat interval, send heartbeat if interval has elapsed

    // Heartbeats send events to a message group for discovery and monitoring
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

