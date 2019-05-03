package coyote.commons.tracing.propagation;


/**
 * {@link TextMapInject} is a built-in carrier for {@link coyote.commons.tracing.Tracer#inject} only.
 * {@link TextMapInject} implementations allows Tracers to read key:value String
 * pairs from arbitrary underlying sources of data.
 */
public interface TextMapInject {

  /**
   * Puts a key:value pair into the TextMapWriter's backing store.
   *
   * @param key   a String, possibly with constraints dictated by the particular Format this TextMap is paired with
   * @param value a String, possibly with constraints dictated by the particular Format this TextMap is paired with
   */
  void put(String key, String value);
}
