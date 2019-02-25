package coyote.kestrel.transport;

import coyote.commons.StringUtil;
import coyote.loader.cfg.Config;
import coyote.loader.log.Log;

import java.net.URI;
import java.net.URISyntaxException;

public class TransportConfig extends Config {
    private String brokerURI = null;
    private URI currentURI = null;

    public TransportConfig setURI(String uri) {
        brokerURI = uri;
        isValid();
        return this;
    }

    public String getURI() {
        return brokerURI;
    }




    public boolean isValid() {
        boolean retval = true;
        try {
            URI uri = new URI(brokerURI);
            if (StringUtil.isNotBlank(uri.getScheme())) {
                if (Transport.AMQP.equalsIgnoreCase(uri.getScheme())) {
                    // perform AMQP checks
                } else if (Transport.JMS.equalsIgnoreCase(uri.getScheme())) {
                    // perform JMS checks
                } else {
                    retval = false;
                    Log.warn("The broker URI scheme is not supported: '" + brokerURI + "'");
                }
            } else {
                retval = false;
                Log.warn("The broker URI scheme is blank: '" + brokerURI + "'");
            }
        } catch (URISyntaxException e) {
            retval = false;
            Log.warn("The broker URI is invalid: '" + brokerURI + "'");
        }
        return retval;
    }


}
