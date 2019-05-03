package coyote.commons.tracing.propagation;




import java.util.Map;

/**
 * A TextMap carrier for use with Tracer.inject() ONLY (it has no read methods).
 *
 * <p>Note that the TextMap interface can be made to wrap around arbitrary data types (not just Map&lt;String, String&gt;
 * as illustrated here).
 */
public class TextMapInjectAdapter implements TextMapInject {
  protected final Map<String, ? super String> map;

  public TextMapInjectAdapter(final Map<String, ? super String> map) {
    this.map = map;
  }

  @Override
  public void put(String key, String value) {
    this.map.put(key, value);
  }
}
