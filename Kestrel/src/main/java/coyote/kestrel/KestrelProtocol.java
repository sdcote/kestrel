package coyote.kestrel;

import coyote.dataframe.DataFrameException;
import coyote.kestrel.transport.Message;

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
  public static final String GENERIC_DATA_FIELD = "DATA";

  // Types of messages there are
  public static final String ACK_TYPE = "ACK"; // Acknowledgement - OK
  public static final String NAK_TYPE = "NAK"; // Negative Acknowledgement - Not OK
  public static final String OAM_TYPE = "OAM"; // Negative Acknowledgement - Not OK



  private KestrelProtocol() {
  }





  public static Message createResponse(Message request) {
    if (request != null) {
      Message retval = new Message();
      if (request.getReplyGroup() != null) {
        retval.setGroup(request.getReplyGroup());
      }

      if (request.getId() != null) {
        retval.setReplyId(request.getId());
      }

      retval.generateId();

      return retval;
    } else {
      return null;
    }
  }



  public static Message createAck(Message cmd) {
    Message retval = createResponse(cmd);
    retval.setType(ACK_TYPE);
    return retval;
  }




  public static Message createNak(String replyGroup, String replyId, String requestId, String failureMessage, Integer errorCode) {
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




  public static Message createNak(Message message, String msg) {
    Message retval = createResponse(message);
    retval.setType(NAK_TYPE);
    if (msg != null && msg.trim().length() > 0) {
      retval.put(MESSAGE_FIELD, msg);
    }

    return retval;
  }




  public static Message createNak(Message message, int code) {
    Message retval = createResponse(message);
    retval.setType(NAK_TYPE);
    retval.put(RESULT_CODE_FIELD, code);
    return retval;
  }




  public static Message createNak(Message message, int code, String msg) {
    Message retval = createNak(message, code);
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
