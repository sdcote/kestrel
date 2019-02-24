package coyote.profile;

import coyote.dataframe.DataFrameException;
import coyote.profile.transport.Message;

import java.util.UUID;

/**
 * This is a class of static methods to perform protocol related functions.
 */
public class KestrelProtocol {

  // Standardized field names
  public static final String IDENTIFIER_FIELD = "MID"; // transport specific message identifier
  public static final String ID_FIELD= "ID"; // application specific identifier for the message/packet
  public static final String REPLY_ID_FIELD = "RPID"; // message/packet for which this message is a reply
  public static final String SOURCE_FIELD = "SRC";
  public static final String TARGET_FIELD = "TGT";
  public static final String GROUP_FIELD = "GRP";
  public static final String REPLY_GROUP_FIELD = "RPY"; // name of the group to which replies should be sent
  public static final String FLAGS_FIELD = "FLG";
  public static final String PRIORITY_FIELD = "PRI";
  public static final String TYPE_FIELD = "TYP";
  public static final String MESSAGE_FIELD = "MSG";
  public static final String RESULT_CODE_FIELD = "RSLTCD";

  // Types of packets there are
  public static final String ACK_TYPE = "ACK"; // Acknowledgement - OK
  public static final String NAK_TYPE = "NAK"; // Negative Acknowledgement - Not OK



  public static final short LOWEST = 0;
  public static final short VERY_LOW = 1;
  public static final short LOW = 2;
  public static final short BELOW_NORMAL = 3;
  public static final short NORMAL = 4;
  public static final short ABOVE_NORMAL = 5;
  public static final short HIGH = 6;
  public static final short VERY_HIGH = 7;
  public static final short HIGHEST = 8;
  private static final String[] priorityNames = new String[]{"Lowest", "Very Low", "Low", "Below Normal", "Normal", "Above Normal", "High", "Very High", "Highest"};


  private KestrelProtocol() {
  }




  public static Message createResponsePacket(Message packet) {
    Message retval = packet.createResponse();
    if (packet.contains(REPLY_GROUP_FIELD)) {
      retval.put(GROUP_FIELD, packet.getAsString(REPLY_GROUP_FIELD));
    }

    if (packet.contains(ID_FIELD)) {
      retval.put(REPLY_ID_FIELD, packet.getAsString(ID_FIELD));
    }

    retval.put(ID_FIELD, UUID.randomUUID().toString());
    return retval;
  }




  public static Message createAck(Message cmd) {
    Message retval = createResponsePacket(cmd);
    retval.setType(ACK_TYPE);
    return retval;
  }




  public static Message createNak(String replyGroup, String replyId, byte[] requestId, String failureMessage, Integer errorCode) {
    Message retval = new Message();
    if (replyGroup != null) {
      retval.setGroup(replyGroup);
    }

    if (requestId != null) {
      retval.setReplyId(requestId);
    }

    retval.setType(NAK_TYPE);
    if (failureMessage != null && failureMessage.trim().length() > 0) {
      retval.put(MESSAGE_FIELD, failureMessage);
    }

    if (replyId != null) {
      retval.put(REPLY_ID_FIELD, replyId);
    }

    if (errorCode != null) {
      retval.put(RESULT_CODE_FIELD, errorCode);
    }

    retval.put(ID_FIELD, UUID.randomUUID().toString());
    return retval;
  }




  public static Message createNak(Message packet, String msg) {
    Message retval = createResponsePacket(packet);
    retval.setType(NAK_TYPE);
    if (msg != null && msg.trim().length() > 0) {
      retval.put(MESSAGE_FIELD, msg);
    }

    return retval;
  }




  public static Message createNak(Message packet, int code) {
    Message retval = createResponsePacket(packet);
    retval.setType(NAK_TYPE);
    retval.put(RESULT_CODE_FIELD, code);
    return retval;
  }




  public static Message createNak(Message packet, int code, String msg) {
    Message retval = createNak(packet, code);
    if (msg != null && msg.trim().length() > 0) {
      retval.put(MESSAGE_FIELD, msg);
    }

    return retval;
  }




  public static void checkForException(Message p) throws RequestException {
    String responseType = p.getType();
    if (NAK_TYPE.equals(responseType) && p.contains(RESULT_CODE_FIELD)) {
      try {
        throw new RequestException(p.getAsInt(RESULT_CODE_FIELD));
      } catch (DataFrameException ex) {
        throw new RequestException(-1);
      }
    }
  }

}
