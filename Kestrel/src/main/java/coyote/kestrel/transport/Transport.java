/*
 * Copyright (c) 2016 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.kestrel.transport;

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
   * Create a private channel we can use to receive messages.
   */
  public Inbox createInboxChannel();


  /**
   * Open the transport for operation initializing whatever resources are
   * necessary.
   *
   * <p>A connection to the broker is made, but messages do not start flowing
   * until message channels are initialized.</p>
   */
  public void open();


  /**
   * Terminate the connection to the broker, closing any resources that were
   * allocated during the transports operation.
   *
   * <p>The transport can be opened again later.</p>
   */
  public void close();


  /**
   * Get a message channel with a queue quality of service.
   * <p>Queues are durable, non-exclusive, and remain on the server; suitable for service implementations.</p>
   *
   * @param name
   * @return
   */
  public MessageQueue getServiceQueue(String name);


  /**
   * Get a message channel with a Pub/Sub quality of service.
   *
   * <p>Message will not start flowing until the topic is started and a
   * listener is attached.</p>
   *
   * @param name
   * @return
   */
  public MessageTopic getTopic(String name);

}