package coyote.profile;

import coyote.kestrel.protocol.KestrelProtocol;

/**
 * This is a static class that contains all the details relating to how the profile service operates.
 */
public class ProfileProtocol {

    /**
     * This is the name of the message group all requests for profiles.
     *
     * <p>It is used by the service code and the client proxy to ensure they
     * both use the same value.</p>
     */
    public static final String PROFILE_GROUP = "SVC.PROFILE";

    /**
     * The GET command to signal profile retrieval.
     */
    public static final String GET_CMD = "GET"; // set to upper case to help match all requests

    /**
     * The identifier parameter in the request.
     */
    public static final String ID_FIELD = KestrelProtocol.ID_FIELD; // same as used in other parts of the protocol

}
