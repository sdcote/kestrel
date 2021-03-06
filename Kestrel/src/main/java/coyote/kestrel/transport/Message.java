package coyote.kestrel.transport;

import coyote.dataframe.DataFrame;
import coyote.dataframe.DataFrameException;
import coyote.kestrel.protocol.KestrelProtocol;

import java.util.UUID;

public class Message extends DataFrame {

  volatile long timestamp = 0L;
  private byte priority = 4;
  private String cachedGroup = null;
  private String cachedType = null;


  public String getType() {
    if (cachedType == null && getObject(KestrelProtocol.TYPE_FIELD) != null) {
      cachedType = getObject(KestrelProtocol.TYPE_FIELD).toString();
    }
    return cachedType;
  }

  public void setType(String name) {
    this.put(KestrelProtocol.TYPE_FIELD, name);
    this.cachedType = name;
  }

  public String getGroup() {
    if (cachedGroup == null && getObject(KestrelProtocol.GROUP_FIELD) != null) {
      cachedGroup = getObject(KestrelProtocol.GROUP_FIELD).toString();
    }
    return cachedGroup;
  }

  public void setGroup(String name) {
    put(KestrelProtocol.GROUP_FIELD, name);
    cachedGroup = name;
  }

  public String getReplyGroup() {
    return this.getObject(KestrelProtocol.REPLY_GROUP_FIELD) != null ? getObject(KestrelProtocol.REPLY_GROUP_FIELD).toString() : null;
  }

  public void setReplyGroup(String name) {
    this.put(KestrelProtocol.REPLY_GROUP_FIELD, name);
  }

  public String getId() {
    return super.getAsString(KestrelProtocol.IDENTIFIER_FIELD);
  }

  public void setId(String id) {
    put(KestrelProtocol.IDENTIFIER_FIELD, id);
  }

  public String getReplyId() {
    return super.getAsString(KestrelProtocol.REPLY_ID_FIELD);
  }

  public void setReplyId(String rid) {
    put(KestrelProtocol.REPLY_ID_FIELD, rid);
  }

  public String getEncoding() {
    return super.getAsString(KestrelProtocol.ENCODING_FIELD);
  }

  public void setEncoding(String enc) {
    put(KestrelProtocol.ENCODING_FIELD, enc);
  }

  /**
   * The Unix time when this message should be considered expired and not
   * processed.
   *
   * @return Unix time (epoch time in seconds) when the message is
   * considered stale and should not be serviced.
   */
  public long getExpiry() {
    try {
      return super.getAsLong(KestrelProtocol.EXPIRY_FIELD);
    } catch (DataFrameException e) {
      return 0L;
    }
  }

  /**
   * Set the Unix time when this message is to expire.
   *
   * @param seconds the number of seconds past 00:00:00 Thursday, 1 January 1970 when this message is to expire.
   */
  public void setExpiry(Long seconds) {
    if (seconds > 0) {
      put(KestrelProtocol.EXPIRY_FIELD, seconds);
    }
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
      retval = getAsFrame(KestrelProtocol.PAYLOAD_FIELD);
      if (retval == null) retval = new DataFrame();
    } catch (DataFrameException ignore) {
      retval = new DataFrame();
      super.remove(KestrelProtocol.PAYLOAD_FIELD);
    }
    return retval;
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
    put(KestrelProtocol.PAYLOAD_FIELD, frame);
  }

  public String generateId() {
    setId(UUID.randomUUID().toString());
    return getId();
  }

  public String getMessage() {
    return super.getAsString(KestrelProtocol.MESSAGE_FIELD);
  }

  public void setMessage(String msg) {
    this.put(KestrelProtocol.MESSAGE_FIELD, msg);
  }

  public int getResultCode() {
    try {
      return super.getAsInt(KestrelProtocol.RESULT_CODE_FIELD);
    } catch (DataFrameException e) {
      return 0;
    }
  }

  public void setResultCode(int resultCode) {
    this.put(KestrelProtocol.RESULT_CODE_FIELD, resultCode);
  }


  /**
   * Check to see if the current time is greater than the messages expiration time.
   *
   * <p>The message may contain a timestamp in Unix time</p>
   *
   * @return true if the message is expired and should not be processed, false
   * if the message is still "fresh" or there is no expiry timestamp.
   */
  public boolean isExpired() {
    boolean retval = false;
    if (contains(KestrelProtocol.EXPIRY_FIELD) && getExpiry() > 0) {
      long expiry = getExpiry();
      retval = expiry > 0 && System.currentTimeMillis() / 1000 > expiry;
    }
    return retval;
  }

  public void setSource(String text) {
    this.put(KestrelProtocol.SOURCE_FIELD, text);
  }
}

