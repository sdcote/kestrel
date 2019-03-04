package coyote.kestrel.transport;

import coyote.commons.StringUtil;
import coyote.loader.cfg.Config;
import coyote.loader.log.Log;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;
import java.util.Map;

/**
 * This builder caches connections to brokers to keep resources under control.
 */
public class TransportBuilder extends Config {
  private String scheme = null;
  private String username = null;
  private String password = null;
  private int port = 0;
  private String host = null;
  private String query = null;

  static final Map<String, Transport> transportMap = new Hashtable<>();

  public TransportBuilder setURI(String uri) throws IllegalArgumentException {
    try {
      URI brokerURI = new URI(uri);
      setURI(brokerURI);
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException("The broker URI string is invalid: '" + e.getMessage() + "'");
    }
    return this;
  }

  public TransportBuilder setURI(URI uri) throws IllegalArgumentException {

    return this;
  }

  public String getURI() {
    return null;
  }


  public Transport build() throws IllegalArgumentException {
    Transport retval = null;

    String brokerScheme = getScheme();
    String brokerUsername = getUsername();
    String brokerPassword = getPassword();
    String brokerHost = getHost();
    int brokerPort = getPort();
    String brokerQuery = getQuery();

    if (StringUtil.isNotBlank(getScheme())) {
      if (Transport.AMQP.equalsIgnoreCase(getScheme()) || Transport.AMQPS.equalsIgnoreCase(getScheme())) {
        retval = createAmqpTransport();
      } else if (Transport.JMS.equalsIgnoreCase(getScheme())) {
       retval = createJmsTransport();
      } else {
        Log.warn("The broker scheme is not supported: '" + getScheme() + "'");
      }
    } else {
      Log.warn("The broker scheme is blank or empty");
    }
    return retval;
  }

  private Transport createJmsTransport() {
    return null;
  }

  private Transport createAmqpTransport() {
    return null;
  }

  private String getQuery() {
    return query;
  }

  private int getPort() {
    return port;
  }

  private String getHost() {
    return host;
  }

  private String getPassword() {
    return password;
  }

  private String getUsername() {
    return username;
  }




  public TransportBuilder setScheme(String scheme) {
    return this;
  }

  public String getScheme() {
    return scheme;
  }

  public TransportBuilder setUsername(String username) {
    return this;
  }

  public TransportBuilder setPassword(String password) {
    return this;
  }

  public TransportBuilder setHost(String hostname) {
    return this;
  }

  public TransportBuilder setPort(int port) {
    return this;
  }


  public TransportBuilder setQuery(String hostname) {
    return this;
  }


  // This may be AMQP specific
  public TransportBuilder setVirtualHost(String virtualHost) {
    return this;
  }

  // timeout in milliseconds; zero for infinite
  public TransportBuilder setConnectionTimeout(int timeout) {
    return this;
  }

}
