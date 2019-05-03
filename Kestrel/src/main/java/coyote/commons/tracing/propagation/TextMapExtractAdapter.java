package coyote.commons.tracing.propagation;


import java.util.Iterator;
import java.util.Map;

/**
 * A TextMap carrier for use with Tracer.extract() ONLY (it has no mutating methods).
 * <p>
 * Note that the TextMap interface can be made to wrap around arbitrary data types (not just Map&lt;String, String&gt;
 * as illustrated here).
 */
public class TextMapExtractAdapter implements TextMapExtract {
  protected final Map<String, String> map;

  public TextMapExtractAdapter(final Map<String, String> map) {
    this.map = map;
  }

  @Override
  public Iterator<Map.Entry<String, String>> iterator() {
    return map.entrySet().iterator();
  }
}
