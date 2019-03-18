package coyote.kestrel.protocol;

import coyote.kestrel.protocol.ProtocolException;

public class RequestException extends ProtocolException {
  public static final long serialVersionUID = 1L;
  private String userMessage;


  public RequestException() {
  }

  public RequestException(int code) {
    super(code);
  }

  public RequestException(int code, String userMessage) {
    super(code);
    this.userMessage = userMessage;
  }

  public RequestException(String msg) {
    super(msg);
  }

  public RequestException(String msg, int code) {
    super(msg, code);
  }

  public RequestException(String msg, Throwable cause) {
    super(msg, cause);
  }

  public RequestException(String msg, Throwable cause, int code) {
    super(msg, cause, code);
  }

  public RequestException(Throwable cause) {
    super(cause);
  }

  public RequestException(Throwable cause, int code) {
    super(cause, code);
  }

  public String getUserMessage() {
    return this.userMessage;
  }
}
