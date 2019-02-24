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

  void setURI(URI uri);


  /**
   * Create a private group we can use to receive messages.
   *
   * @return A name of a group on which anyone can send but only we can
   *         receive.
   */
  public String createInboxGroup();



  /**
   * Open the transport for operation initializing whatever resources are
   * necessary.
   */
  public void open();


  /**
   * Terminate the operation of the transport closing any resources that were
   * allocated during the transports operation.
   */
  public void close();


}