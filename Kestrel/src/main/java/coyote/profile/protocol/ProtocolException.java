package coyote.profile.protocol;


public class ProtocolException extends Exception {
  public static final long serialVersionUID = 1L;
  int resultCode = -1;




  public ProtocolException() {
  }




  public ProtocolException(int code) {
    //super(ResultCode.getLocalizedMessage(code));
    this.resultCode = code;
  }




  public ProtocolException(String msg) {
    super(msg);
  }




  public ProtocolException(String msg, int code) {
    super(msg);
    this.resultCode = code;
  }




  public ProtocolException(String msg, Throwable cause) {
    super(msg, cause);
    this.extractResultCode(cause);
  }




  public ProtocolException(String msg, Throwable cause, int code) {
    super(msg, cause);
    this.resultCode = code;
  }




  public ProtocolException(Throwable cause) {
    super(cause);
    this.extractResultCode(cause);
  }




  public ProtocolException(Throwable cause, int code) {
    super(cause);
    this.resultCode = code;
  }




  protected void extractResultCode(Throwable err) {
    if (err != null && err instanceof ProtocolException && this.resultCode == -1) {
      this.resultCode = ((ProtocolException)err).getResultCode();
    }

  }




  public int getResultCode() {
    return this.resultCode;
  }




  public String getMessage() {
    String resultMessage = null;//this.getResultMessage();
    if (super.getMessage() != null && super.getMessage().length() > 0) {
      String message = super.getMessage();
      if (resultMessage != null && message != null && !resultMessage.equals(message)) {
        resultMessage = resultMessage + " - " + message;
      }
    }
    return resultMessage;
  }


}
