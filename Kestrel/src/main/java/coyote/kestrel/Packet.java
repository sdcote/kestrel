package coyote.kestrel;

import coyote.commons.ByteUtil;
import coyote.dataframe.DataFrame;

public class Packet extends DataFrame {

  private byte priority = 4;

  volatile long timestamp = 0L;
  private String cachedGroup = null;
  private String cachedType = null;


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
    return retval != null ? (byte[])retval : null;
  }

  public String getIdString() {
    Object retval = this.getObject(KestrelProtocol.IDENTIFIER_FIELD);
    return retval != null ? ByteUtil.bytesToHex((byte[])retval) : null;
  }

  public byte[] getReplyId() {
    Object retval = this.getObject(KestrelProtocol.REPLY_ID_FIELD);
    return retval != null ? (byte[])retval : null;
  }

  public void setReplyId(byte[] id) {
    this.put(KestrelProtocol.REPLY_ID_FIELD, id);
  }

  public String getReplyIdString() {
    Object retval = this.getObject(KestrelProtocol.REPLY_ID_FIELD);
    return retval != null ? ByteUtil.bytesToHex((byte[])retval) : null;
  }

  public Packet createResponse() {
    return createResponse(this);
  }


  public static Packet createResponse(Packet request) {
    if (request != null) {
      Packet retval = new Packet();
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


}
