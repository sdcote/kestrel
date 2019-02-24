package coyote.kestrel;

import coyote.commons.ExceptionUtil;
import coyote.kestrel.protocol.MessageGroup;
import coyote.kestrel.transport.Inbox;
import coyote.kestrel.transport.Message;
import coyote.loader.AbstractLoader;
import coyote.loader.cfg.ConfigurationException;
import coyote.loader.log.Log;


/**
 * This is a base class for all services; handling much of the infrastructure.
 */
public abstract class AbstractService extends AbstractLoader implements KestrelService {

  private static final int DEFAULT_HEARTBEAT_INTERVAL = 300;

  private static volatile boolean running = true;

  /**
   * number of seconds between service heartbeats.
   */
  private int heartbeatInterval = DEFAULT_HEARTBEAT_INTERVAL;
  private volatile long lastHeartbeat = 0;

  protected ServiceMessageGroup serviceGroup = new ServiceMessageGroup();
  protected Inbox inbox = new Inbox();

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
    Log.info("Abstract service ran");

    // connect to the message broker on the appropriate queue

    while (running) {

      heartbeat();

      // Pull a message from our inbox to see if we have an OAM message to process
      Message oam = inbox.getNextMessage();
      while (oam != null) {
        try {
          processInboxMessage(oam);
        } catch (final Exception e) {
          Log.error(ExceptionUtil.toString(e));
          if (Log.isLogging(Log.DEBUG_EVENTS)) {
            Log.debug(ExceptionUtil.stackTrace(e));
          }
        } // try-catch

      }

      // pull a message from the queue, block only for a short time

      Message packet = new Message();

      try {
        process(packet);
      } catch (final Exception e) {
        //Log.error(LogMsg.createMsg(CDX.MSG, "Job.exception_running_engine", e.getClass().getSimpleName(), e.getMessage(), engine.getName(), engine.getClass().getSimpleName()));
        Log.error(ExceptionUtil.toString(e));
        if (Log.isLogging(Log.DEBUG_EVENTS)) {
          Log.debug(ExceptionUtil.stackTrace(e));
        }
      } // try-catch
    }


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
    try {
      onShutdown();
    } catch (Throwable ball) {
      Log.warn(ball);
    }
  }

}

