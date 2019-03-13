package coyote.kestrel.proxy;

import coyote.kestrel.transport.Transport;

/**
 * This represents the Kestrel related processing
 */
public interface KestrelProxy {

  void setTransport(Transport transport);
}
