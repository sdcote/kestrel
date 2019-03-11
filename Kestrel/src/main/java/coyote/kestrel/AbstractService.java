package coyote.kestrel;

import coyote.commons.ExceptionUtil;
import coyote.kestrel.protocol.MessageGroup;
import coyote.kestrel.transport.*;
import coyote.loader.AbstractLoader;
import coyote.loader.cfg.Config;
import coyote.loader.cfg.ConfigurationException;
import coyote.loader.log.Log;


/**
 * This is a base class for all services; handling much of the infrastructure.
 */
public abstract class AbstractService extends AbstractLoader implements KestrelService {

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
  protected Inbox inbox = null;
  /**
   * The number of seconds between service heartbeats.
   */
  private int heartbeatInterval = DEFAULT_HEARTBEAT_INTERVAL;
  /**
   * Time of our last heartbeat
   */
  private volatile long lastHeartbeat = 0;

  /**
   * The message broker connection
   */
  protected Transport transport = null;

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
        inboxProcessing();
        coherenceProcessing();
        serviceGroupProcessing();
        coherenceProcessing();
      }
    } else {
      Log.fatal("Could not connect to broker");
    }
  }

  private void serviceGroupProcessing() {
    Message message = serviceGroup.getNextMessage();
    if (message != null) {
      try {
        process(message);
      } catch (final Exception e) {
        //Log.error(LogMsg.createMsg(CDX.MSG, "Job.exception_running_engine", e.getClass().getSimpleName(), e.getMessage(), engine.getName(), engine.getClass().getSimpleName()));
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
      inbox = getTransport().createInboxChannel();
    } catch (Exception e) {
      running = false;
      Log.error("Could not initialize the inbox group");
    }
  }


  private Transport getTransport() {
    if (transport == null) {
      try {
        Config cfg = configuration.getSection(KestrelService.TRANSPORT_SECTION);
        String transportUri = cfg.getString(KestrelService.URI_TAG, true);
        transport = new TransportBuilder().setURI(transportUri).build();
        transport.open();
      } catch (Exception e) {
        Log.error("Could not build transport: " + e.getLocalizedMessage());
        transport = new InvalidTransport();
      }
    }

    return transport;
  }


  protected void respond(Message response) {
    serviceGroup.respond(response);
  }

  protected void send(Message response) {
    serviceGroup.send(response);
  }

  protected void sendNak(Message message, String msg) {
  }

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

