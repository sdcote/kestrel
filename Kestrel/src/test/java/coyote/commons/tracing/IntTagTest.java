package coyote.commons.tracing;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class IntTagTest {
  @Test
  public void testSetInt() {
    Integer value = 7;
    String key = "expected.key";
    Span span = mock(Span.class);

    IntTag tag = new IntTag(key);
    tag.set(span, value);

    verify(span).setTag(key, value);
  }
}