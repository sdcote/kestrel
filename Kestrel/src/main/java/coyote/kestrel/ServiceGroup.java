package coyote.kestrel;

import coyote.kestrel.protocol.KestrelServiceGroup;
import coyote.kestrel.protocol.MessageGroup;

/**
 * This is a generic service message group which can be used by any service.
 * All that is required is the name of the message group to join and listen
 * for messages.
 *
 * <p>A large majority of the processing is handled by the abstract base class
 * which provides uniformity between service group instances.</p>
 */
public class ServiceGroup extends KestrelServiceGroup implements MessageGroup {

  public ServiceGroup(String groupName) {
    this.groupName = groupName;
  }

}
