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

  void initialize();
}