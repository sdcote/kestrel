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
    private String brokerURI = null;
    private URI currentURI = null;

    static final Map<String, Transport> transportMap = new Hashtable<>();

    public TransportBuilder setURI(String uri) {
        brokerURI = uri;
        return this;
    }

    public String getURI() {
        return brokerURI;
    }


    public Transport build() throws IllegalArgumentException {
        Transport retval = null;
        try {
            URI uri = new URI(brokerURI);
            if (StringUtil.isNotBlank(uri.getScheme())) {
                if (Transport.AMQP.equalsIgnoreCase(uri.getScheme())) {
                    // perform AMQP checks
                } else if (Transport.JMS.equalsIgnoreCase(uri.getScheme())) {
                    // perform JMS checks
                } else {
                    Log.warn("The broker URI scheme is not supported: '" + brokerURI + "'");
                }
            } else {
                Log.warn("The broker URI scheme is blank: '" + brokerURI + "'");
            }
        } catch (URISyntaxException e) {
            Log.warn("The broker URI is invalid: '" + brokerURI + "'");
        }
        return retval;
    }


    public TransportBuilder setScheme(String scheme) {
        return this;
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

    // This may be AMQP specific
    public TransportBuilder setVirtualHost(String virtualHost) {
        return this;
    }

    // timeout in milliseconds; zero for infinite
    public TransportBuilder setConnectionTimeout(int timeout) {
        return this;
    }
}
