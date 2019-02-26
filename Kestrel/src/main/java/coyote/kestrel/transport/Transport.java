/*
 * Copyright (c) 2016 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.kestrel.transport;

import java.net.URI;

/**
 * A transport is an adapter to the various messaging transports.
 */
public interface Transport {

  String AMQP = "AMQP";
  String AMQPS = "AMQPS";
  String JMS = "JMS"; // Java message service
  String TIBRV = "TRV"; // Tibco Rendezvous


  void setURI(URI uri);


  /**
   * Create a private channel we can use to receive messages.
   *
   * @return A name of a group on which anyone can send but only we can
   * receive.
   */
  public MessageChannel createInboxGroup();


  /**
   * Open the transport for operation initializing whatever resources are
   * necessary.
   *
   * <p>Messages start flowing once the transport is opened.</p>
   */
  public void open();


  /**
   * Terminate the operation of the transport closing any resources that were
   * allocated during the transports operation.
   *
   * <p>The transport can be opened again later as this method is expected to
   * be called anytime the flow of messages is to be paused. Messages will
   * remain on the broker until the transport is opened again.</p>
   */
  public void close();


  /**
   * Get a message channel with a queue quality of service.
   *
   * @param name
   * @return
   */
  public MessageQueue getQueue(String name);


  /**
   * Get a message channel with a Pub/Sub quality of service.
   *
   * @param name
   * @return
   */
  public MessageTopic getTopic(String name);

}