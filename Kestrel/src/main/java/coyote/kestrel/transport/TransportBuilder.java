package coyote.kestrel.transport;

import coyote.commons.StringUtil;
import coyote.commons.UriUtil;
import coyote.kestrel.transport.amqp.AmqpTransport;
import coyote.loader.log.Log;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class TransportBuilder {
  private static final Map<String, Transport> transportMap = new Hashtable<>();
  private String scheme = null;
  private String username = null;
  private String password = null;
  private int port = 0;
  private String host = null;
  private String query = null;
  private int connectionTimeout = 60000;
  private List<URI> failoverUri = new ArrayList<>();


  public String getQuery() {
    return query;
  }

  public TransportBuilder setQuery(String path) {
    this.query = path;
    return this;
  }

  public int getPort() {
    return port;
  }

  public TransportBuilder setPort(int port) {
    this.port = port;
    return this;
  }

  public String getHostname() {
    return host;
  }

  public String getPassword() {
    return password;
  }

  public TransportBuilder setPassword(String password) {
    this.password = password;
    return this;
  }

  public String getUsername() {
    return username;
  }

  public TransportBuilder setUsername(String username) {
    this.username = username;
    return this;
  }

  public String getScheme() {
    return scheme;
  }

  public TransportBuilder setScheme(String scheme) {
    this.scheme = scheme;
    return this;
  }

  public TransportBuilder setHost(String host) {
    this.host = host;
    return this;
  }

  public int getConnectionTimeout() {
    return connectionTimeout;
  }

  // timeout in milliseconds; zero for infinite
  public TransportBuilder setConnectionTimeout(int timeout) {
    this.connectionTimeout = timeout;
    return this;
  }

  public Transport createJmsTransport() {
    return null;
  }

  public Transport createAmqpTransport() {
    AmqpTransport retval = new AmqpTransport();
    URI brokerUri = null;

    // Craft a URI from our data

    retval.AddUri(brokerUri);
    for (URI uri : failoverUri) {
      retval.AddUri(uri);
    }
    return retval;
  }


  public TransportBuilder setURI(String uri) throws IllegalArgumentException {
    if (StringUtil.isNotEmpty(uri)) {
      try {
        URI brokerURI = new URI(uri);
        setURI(brokerURI);
      } catch (URISyntaxException e) {
        throw new IllegalArgumentException("The broker URI string is invalid: '" + e.getMessage() + "'");
      }
    } // ignore null/empty uri strings
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
      retval = new InvalidTransport();
    }
    return retval;
  }

  public TransportBuilder addFailover(String uri) {
    if (StringUtil.isNotEmpty(uri)) {
      try {
        URI brokerURI = new URI(uri);
        addFailover(brokerURI);
      } catch (URISyntaxException e) {
        throw new IllegalArgumentException("The broker URI string is invalid: '" + e.getMessage() + "'");
      }
    } // ignore null/empty uri strings
    return this;

  }


  public TransportBuilder addFailover(URI uri) {
    for (URI furi : failoverUri) {
      if (furi.equals(uri)) {
        return this;
      }
    }
    failoverUri.add(uri);
    return this;
  }

}
