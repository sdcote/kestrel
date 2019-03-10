package coyote.kestrel.transport;


/**
 * This represents a private queue on which anybody can publish, but only we
 * can subscribe.
 *
 * <p>An inbox is a way to communicate directly to a component in the system.</p>
 *
 * <p>Every service has an inbox in which OAM (operations, administration and
 * maintenance) commands are received. OAM commands can be used to terminate a
 * service instance, instruct it to perform a backup, enter a message into its
 * logging stream or perform other processing outside of the service it
 * provides.</p>
 */
public interface Inbox {

  /**
   * This retrieves the next message from the inbox.
   *
   * <p>This should return immediately, with no blocking.</p>
   *
   * @return the next message waiting in the inbox, or null if there are no messages waiting.
   */
   Message getNextMessage();

}
