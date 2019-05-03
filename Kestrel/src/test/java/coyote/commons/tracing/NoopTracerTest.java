package coyote.commons.tracing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

public class NoopTracerTest {
  @Test
  public void activeSpanValueToleratesUseTest() {
    try {
      final Span activeSpan = NoopTracerImpl.INSTANCE.activeSpan();
      assertNotNull(activeSpan);
      Tags.ERROR.set(activeSpan, true);
    } catch (final NullPointerException e) {
      fail("NoopTracer.activeSpan() should return a usable span");
    }
  }
}