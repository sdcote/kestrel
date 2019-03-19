/*
 * Copyright (c) 2016 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.kestrel.transport;

import java.io.IOException;

/**
 * A transport is an adapter to the various messaging transports.
 * <p>
 * Transports must be opened before using them.
 */
public interface Transport {

  String AMQP = "AMQP";
  String AMQPS = "AMQPS";
  String JMS = "JMS"; // Java message service
  String TIBRV = "TRV"; // Tibco Rendezvous


  /**
   * @return true if the transport is open and functioning, false otherwise.
   */
  boolean isValid();


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
  MessageQueue createInbox();


  /**
   * Open the transport for operation initializing whatever resources are
   * necessary.
   *
   * <p>A connection to the broker is made, but messages do not start flowing
   * until message channels are initialized.</p>
   */
  void open();


  /**
   * Terminate the connection to the broker, closing any resources that were
   * allocated during the transports operation.
   *
   * <p>The transport can be opened again later.</p>
   */
  void close();


  /**
   * Get a message channel with a queue quality of service.
   * <p>Queues are durable, non-exclusive, and remain on the server; suitable
   * for service implementations.</p>
   *
   * <p>Service queues operate on the competing consumer principle, so the
   * service must retrieve a message, process it, then< acknowledge it so it
   * is removed from the queue. If there is no acknowledgement, the message
   * is marked for re-delivery and another consumer can retrieve it./p>
   *
   * @param name the name of the queue to create in the broker.
   * @return a queue from which to retrieve messages.
   */
  MessageQueue getServiceQueue(String name);


  /**
   * Get a message channel with a Pub/Sub quality of service.
   *
   * <p>Message will not start flowing until the topic is started and a
   * listener is attached.</p>
   *
   * @param name
   * @return
   */
  MessageTopic getTopic(String name);


  /**
   * Send the message on this transport to the message group specified in the
   * message.
   *
   * <p>This assumes a queue quality of service. The message should be
   * delivered to the named queue if it exists.</p>
   *
   * @param message the message to send.
   * @throws java.io.IOException if an error is encountered
   */
  void sendDirect(Message message) throws IOException;


  /**
   * Send the message on this transport to the message group specified in the
   * message.
   *
   * <p>This assumes a Topic quality of service. Zero or more consumers may
   * receive this message.</p>
   *
   * @param message the message to send.
   * @throws java.io.IOException if an error is encountered
   */
  void broadcast(Message message) throws IOException;

}