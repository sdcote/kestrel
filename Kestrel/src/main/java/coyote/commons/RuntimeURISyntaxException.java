package coyote.commons;

/**
 * A runtime exception for wrapping java.net.URISyntaxException.
 */
public class RuntimeURISyntaxException extends RuntimeException {

  public RuntimeURISyntaxException(final Throwable cause) {
    super(cause);
  }

  /**
   * We're not interested in the wrapper's stack trace.
   *
   * @return null
   */
  @Override
  public Throwable fillInStackTrace() {
    return null;
  }

}