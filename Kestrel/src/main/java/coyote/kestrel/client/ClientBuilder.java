package coyote.kestrel.client;

public class ClientBuilder {

  // other attributes to refine transport creation

  /**
   * Build a client of the given type connected to the messaging transport currently configured/
   *
   * @param type
   * @param <E>
   * @return the first type which implements the given interface type
   */
  public static <E> E build(Class<E> type) {

    // search for a class which implements the type

    // If that class also implements the KestrelClient interface, configure it

    return type.cast(null);
  }

}
