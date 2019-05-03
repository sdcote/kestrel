package coyote.commons.tracing;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class BooleanTagTest {

  @Test
  public void testSetBoolean() {
    Boolean value = true;
    String key = "expected.key";
    Span span = mock(Span.class);

    BooleanTag tag = new BooleanTag(key);
    tag.set(span, value);

    verify(span).setTag(key, value);
  }
}