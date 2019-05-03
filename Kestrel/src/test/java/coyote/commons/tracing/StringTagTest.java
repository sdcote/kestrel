package coyote.commons.tracing;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class StringTagTest {

  @Test
  public void testSetString() {
    String value = "expected.value";
    String key = "expected.key";

    Span span = mock(Span.class);
    StringTag tag = new StringTag(key);
    tag.set(span, value);

    verify(span).setTag(key, value);
  }

}