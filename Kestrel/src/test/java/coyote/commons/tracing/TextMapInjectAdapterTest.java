package coyote.commons.tracing;

import coyote.commons.tracing.propagation.TextMapInjectAdapter;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TextMapInjectAdapterTest {

  @Test
  public void testPut() {
    Map<String, Object> headers = new LinkedHashMap<String, Object>();
    TextMapInjectAdapter injectAdapter = new TextMapInjectAdapter(headers);
    injectAdapter.put("foo", "bar");

    assertEquals("bar", headers.get("foo"));
  }
}
