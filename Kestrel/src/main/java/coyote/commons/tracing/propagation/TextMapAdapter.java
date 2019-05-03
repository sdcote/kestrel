package coyote.commons.tracing.propagation;


import java.util.Map;

/**
 * A {@link TextMap} carrier for use with {@link coyote.commons.tracing.Tracer#inject} and {@link coyote.commons.tracing.Tracer#extract}.
 */
public class TextMapAdapter extends TextMapExtractAdapter implements TextMap {
  public TextMapAdapter(Map<String, String> map) {
    super(map);
  }

  @Override
  public void put(String key, String value) {
    map.put(key, value);
  }
}
