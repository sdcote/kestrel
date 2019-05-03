package coyote.commons.tracing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class AbstractTagTest {

  @Test
  public void testGetKey() {
    String key = "bar";
    StringTag tag = new StringTag(key);
    assertEquals(key, tag.getKey());
  }

  @Test
  public void testSetTagOnSpan() {
    String value = "foo";
    String key = "bar";

    Span activeSpan = mock(Span.class);
    StringTag tag = new StringTag(key);
    tag.set(activeSpan, value);

    verify(activeSpan).setTag(key, value);
  }
}