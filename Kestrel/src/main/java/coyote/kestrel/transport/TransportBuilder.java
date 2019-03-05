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
  private int connectionTimeout = 60000;

  private String virtualHost = null;

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
    if (uri != null) {
      setScheme(uri.getScheme());
      String userInfo = uri.getUserInfo();
      if (StringUtil.isNotEmpty(userInfo)) {
        if (userInfo.contains(":")) {
          String[] userInfoArray = userInfo.split(":");
          setUsername(userInfoArray[0]);
          setPassword(userInfoArray[1]);
        } else {
          setUsername(userInfo);
        }
      }
      setHost(uri.getHost());
      setPort(uri.getPort());
      setQuery(uri.getPath());
    }

    return this;
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

  public String getQuery() {
    return query;
  }

  public int getPort() {
    return port;
  }

  public String getHost() {
    return host;
  }

  public String getPassword() {
    return password;
  }

  public String getUsername() {
    return username;
  }


  public TransportBuilder setScheme(String scheme) {
    this.scheme = scheme;
    return this;
  }

  public String getScheme() {
    return scheme;
  }

  public TransportBuilder setUsername(String username) {
    this.username = username;
    return this;
  }

  public TransportBuilder setPassword(String password) {
    this.password = password;
    return this;
  }

  public TransportBuilder setHost(String host) {
    this.host = host;
    return this;
  }

  public TransportBuilder setPort(int port) {
    this.port = port;
    return this;
  }


  public TransportBuilder setQuery(String path) {
    this.query = path;
    return this;
  }


  // This may be AMQP specific
  public TransportBuilder setVirtualHost(String virtualHost) {
    this.virtualHost = virtualHost;
    return this;
  }

  public String getVirtualHost() {
    return virtualHost;
  }

  // timeout in milliseconds; zero for infinite
  public TransportBuilder setConnectionTimeout(int timeout) {
    this.connectionTimeout = timeout;
    return this;
  }

  public int getConnectionTimeout() {
    return connectionTimeout;
  }

}
