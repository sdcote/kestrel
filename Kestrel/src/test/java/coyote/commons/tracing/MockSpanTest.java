package coyote.commons.tracing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class MockSpanTest {

  @Test
  public void testSetOperationNameAfterFinish() {
    MockTracer tracer = new MockTracer();
    Span span = tracer.buildSpan("foo").start();
    span.finish();

    try {
      span.setOperationName("bar");
      fail();
    } catch (RuntimeException ex) {
    }
    assertEquals(1, tracer.finishedSpans().get(0).generatedErrors().size());
  }

  @Test
  public void testSetTagAfterFinish() {
    MockTracer tracer = new MockTracer();
    Span span = tracer.buildSpan("foo").start();
    span.finish();

    try {
      span.setTag("bar", "foo");
      fail();
    } catch (RuntimeException ex) {
    }
    assertEquals(1, tracer.finishedSpans().get(0).generatedErrors().size());
  }

  @Test
  public void testAddLogAfterFinish() {
    MockTracer tracer = new MockTracer();
    Span span = tracer.buildSpan("foo").start();
    span.finish();

    try {
      span.log("bar");
      fail();
    } catch (RuntimeException ex) {
    }
    assertEquals(1, tracer.finishedSpans().get(0).generatedErrors().size());
  }

  @Test
  public void testAddBaggageAfterFinish() {
    MockTracer tracer = new MockTracer();
    Span span = tracer.buildSpan("foo").start();
    span.finish();

    try {
      span.setBaggageItem("foo", "bar");
      fail();
    } catch (RuntimeException ex) {
    }
    assertEquals(1, tracer.finishedSpans().get(0).generatedErrors().size());
  }
}