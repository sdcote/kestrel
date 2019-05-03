package coyote.commons.tracing;

import coyote.commons.tracing.propagation.TextMapExtractAdapter;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TextMapExtractAdapterTest {

  @Test
  public void testIterator() {
    Map<String, String> headers = new LinkedHashMap<String, String>();
    headers.put("foo", "bar");
    TextMapExtractAdapter extractAdapter = new TextMapExtractAdapter(headers);

    Iterator<Map.Entry<String, String>> iterator = extractAdapter.iterator();
    assertTrue(iterator.hasNext());
    assertEquals("bar", iterator.next().getValue());
    assertFalse(iterator.hasNext());
  }
}
