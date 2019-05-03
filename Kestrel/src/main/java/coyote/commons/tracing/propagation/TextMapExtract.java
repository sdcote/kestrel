package coyote.commons.tracing.propagation;


import java.util.Iterator;
import java.util.Map;

/**
 * {@link TextMapExtract} is a built-in carrier for {@link coyote.commons.tracing.Tracer#extract} only.
 * {@link TextMapExtract} implementations allows Tracers to write key:value String
 * pairs to arbitrary underlying sources of data.
 */
public interface TextMapExtract extends Iterable<Map.Entry<String, String>> {
  /**
   * Gets an iterator over arbitrary key:value pairs from the TextMapReader.
   *
   * @return entries in the TextMap backing store; note that for some Formats, the iterator may include entries that
   * were never injected by a Tracer implementation (e.g., unrelated HTTP headers)
   */
  Iterator<Map.Entry<String, String>> iterator();
}
