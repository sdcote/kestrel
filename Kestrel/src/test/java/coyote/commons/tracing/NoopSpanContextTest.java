package coyote.commons.tracing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NoopSpanContextTest {
  @Test
  public void traceIdentifierTest() {
    SpanContext ctx = NoopSpanContextImpl.INSTANCE;
    assertEquals("", ctx.toTraceId());
  }

  @Test
  public void spanIdentifierTest() {
    SpanContext ctx = NoopSpanContextImpl.INSTANCE;
    assertEquals("", ctx.toSpanId());
  }
}