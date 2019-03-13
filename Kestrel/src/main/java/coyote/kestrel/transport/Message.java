package coyote.kestrel.transport;

import coyote.commons.ByteUtil;
import coyote.dataframe.DataFrame;
import coyote.dataframe.DataFrameException;
import coyote.kestrel.KestrelProtocol;

public class Message extends DataFrame {

  private byte priority = 4;

  volatile long timestamp = 0L;
  private String cachedGroup = null;
  private String cachedType = null;

  public static final String PAYLOAD_TAG = "PYLD";

  public void setType(String name) {
    this.put(KestrelProtocol.TYPE_FIELD, name);
    this.cachedType = name;
  }

  public String getType() {
    if (cachedType == null && getObject(KestrelProtocol.TYPE_FIELD) != null) {
      cachedType = getObject(KestrelProtocol.TYPE_FIELD).toString();
    }
    return cachedType;
  }

  public void setGroup(String name) {
    put(KestrelProtocol.GROUP_FIELD, name);
    cachedGroup = name;
  }

  public String getGroup() {
    if (cachedGroup == null && getObject(KestrelProtocol.GROUP_FIELD) != null) {
      cachedGroup = getObject(KestrelProtocol.GROUP_FIELD).toString();
    }
    return cachedGroup;
  }

  public void setReplyGroup(String name) {
    this.put(KestrelProtocol.REPLY_GROUP_FIELD, name);
  }

  public String getReplyGroup() {
    return this.getObject(KestrelProtocol.REPLY_GROUP_FIELD) != null ? this.getObject(KestrelProtocol.REPLY_GROUP_FIELD).toString() : null;
  }

  public byte[] getId() {
    Object retval = this.getObject(KestrelProtocol.IDENTIFIER_FIELD);
    return retval != null ? (byte[]) retval : new byte[0];
  }

  public String getIdString() {
    Object retval = this.getObject(KestrelProtocol.IDENTIFIER_FIELD);
    return retval != null ? ByteUtil.bytesToHex((byte[]) retval) : null;
  }

  public byte[] getReplyId() {
    Object retval = this.getObject(KestrelProtocol.REPLY_ID_FIELD);
    return retval != null ? (byte[]) retval : new byte[0];
  }

  public void setReplyId(byte[] id) {
    this.put(KestrelProtocol.REPLY_ID_FIELD, id);
  }

  public String getReplyIdString() {
    Object retval = this.getObject(KestrelProtocol.REPLY_ID_FIELD);
    return retval != null ? ByteUtil.bytesToHex((byte[]) retval) : null;
  }

  public Message createResponse() {
    return createResponse(this);
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

      return retval;
    } else {
      return null;
    }
  }


  /**
   * Serialize a data frame in the message as the payload.
   *
   * <p>Once the payload is set, it becomes immutable within the the message.
   * A copy of the data frame is placed in the message so changing the data
   * frame externally has no effect on the payload.</p>
   *
   * @param frame the data frame to serialize into this message.
   */
  public void setPayload(DataFrame frame) {
    put(PAYLOAD_TAG, frame);
  }

  /**
   * Retrieve a copy of the serialized payload.
   *
   * <p>Changing the returned data frame has no effect on the payload
   * contained in this message. To update the message with the new/updated
   * data frame, it must be set back into the message.</p>
   *
   * <p>If there is a field with the same name of the payload, but it is not a
   * data frame, a new data frame is returned and the existing field is
   * removed. This is by design to help ensure that the payload field is
   * always a data frame. There are valid ways around this of course, just do
   * not call get or set on the payload. Then whatever is placed in that field
   * will remain when sent across the bus.</p>
   *
   * @return an exact copy of the payload serialized in this message or an empty frame if no payload was found.
   */
  public DataFrame getPayload() {
    DataFrame retval;
    try {
      retval = getAsFrame(PAYLOAD_TAG);
      if (retval == null) retval = new DataFrame();
    } catch (DataFrameException ignore) {
      retval = new DataFrame();
      super.remove(PAYLOAD_TAG);
    }
    return retval;
  }


}
