package coyote.commons.tracing;

import coyote.commons.tracing.propagation.*;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class MockTracerTest {
  @Test
  public void testRootSpan() {
    // Create and finish a root Span.
    MockTracer tracer = new MockTracer();
    {
      Span span = tracer.buildSpan("tester").withStartTimestamp(1000).start();
      span.setTag("string", "foo");
      span.setTag("int", 7);
      span.log("foo");
      Map<String, Object> fields = new HashMap<>();
      fields.put("f1", 4);
      fields.put("f2", "two");
      span.log(1002, fields);
      span.log(1003, "event name");
      span.finish(2000);
    }
    List<MockSpan> finishedSpans = tracer.finishedSpans();

    // Check that the Span looks right.
    assertEquals(1, finishedSpans.size());
    MockSpan finishedSpan = finishedSpans.get(0);
    assertEquals("tester", finishedSpan.operationName());
    assertEquals(0, finishedSpan.parentId());
    assertNotEquals(0, finishedSpan.context().traceId());
    assertNotEquals(0, finishedSpan.context().spanId());
    assertEquals(1000, finishedSpan.startMicros());
    assertEquals(2000, finishedSpan.finishMicros());
    Map<String, Object> tags = finishedSpan.tags();
    assertEquals(2, tags.size());
    assertEquals(7, tags.get("int"));
    assertEquals("foo", tags.get("string"));
    List<MockSpan.LogEntry> logs = finishedSpan.logEntries();
    assertEquals(3, logs.size());
    {
      MockSpan.LogEntry log = logs.get(0);
      assertEquals(1, log.fields().size());
      assertEquals("foo", log.fields().get("event"));
    }
    {
      MockSpan.LogEntry log = logs.get(1);
      assertEquals(1002, log.timestampMicros());
      assertEquals(4, log.fields().get("f1"));
      assertEquals("two", log.fields().get("f2"));
    }
    {
      MockSpan.LogEntry log = logs.get(2);
      assertEquals(1003, log.timestampMicros());
      assertEquals("event name", log.fields().get("event"));
    }
  }

  @Test
  public void testChildSpan() {
    // Create and finish a root Span.
    MockTracer tracer = new MockTracer();
    {
      Span parent = tracer.buildSpan("parent").withStartTimestamp(1000).start();
      Span child = tracer.buildSpan("child").withStartTimestamp(1100).asChildOf(parent).start();
      child.finish(1900);
      parent.finish(2000);
    }
    List<MockSpan> finishedSpans = tracer.finishedSpans();

    // Check that the Spans look right.
    assertEquals(2, finishedSpans.size());
    MockSpan child = finishedSpans.get(0);
    MockSpan parent = finishedSpans.get(1);
    assertEquals("child", child.operationName());
    assertEquals("parent", parent.operationName());
    assertEquals(parent.context().spanId(), child.parentId());
    assertEquals(parent.context().traceId(), child.context().traceId());

  }

  @Test
  public void testStartTimestamp() throws InterruptedException {
    MockTracer tracer = new MockTracer();
    long startMicros;
    {
      Tracer.SpanBuilder fooSpan = tracer.buildSpan("foo");
      Thread.sleep(2);
      startMicros = System.currentTimeMillis() * 1000;
      fooSpan.start().finish();
    }
    List<MockSpan> finishedSpans = tracer.finishedSpans();

    assertEquals(1, finishedSpans.size());
    MockSpan span = finishedSpans.get(0);
    assertTrue(startMicros <= span.startMicros());
    assertTrue(System.currentTimeMillis() * 1000 >= span.finishMicros());
  }

  @Test
  public void testStartExplicitTimestamp() throws InterruptedException {
    MockTracer tracer = new MockTracer();
    long startMicros = 2000;
    {
      tracer.buildSpan("foo")
              .withStartTimestamp(startMicros)
              .start()
              .finish();
    }
    List<MockSpan> finishedSpans = tracer.finishedSpans();

    assertEquals(1, finishedSpans.size());
    assertEquals(startMicros, finishedSpans.get(0).startMicros());
  }

  @Test
  public void testTextMapPropagatorTextMap() {
    MockTracer tracer = new MockTracer(MockTracer.Propagator.TEXT_MAP);
    HashMap<String, String> injectMap = new HashMap<>();
    injectMap.put("foobag", "donttouch");
    {
      Span parentSpan = tracer.buildSpan("foo")
              .start();
      parentSpan.setBaggageItem("foobag", "fooitem");
      parentSpan.finish();

      tracer.inject(parentSpan.context(), Format.Builtin.TEXT_MAP_INJECT,
              new TextMapInjectAdapter(injectMap));

      SpanContext extract = tracer.extract(Format.Builtin.TEXT_MAP_EXTRACT, new TextMapExtractAdapter(injectMap));

      Span childSpan = tracer.buildSpan("bar")
              .asChildOf(extract)
              .start();
      childSpan.setBaggageItem("barbag", "baritem");
      childSpan.finish();
    }
    List<MockSpan> finishedSpans = tracer.finishedSpans();

    assertEquals(2, finishedSpans.size());
    assertEquals(finishedSpans.get(0).context().traceId(), finishedSpans.get(1).context().traceId());
    assertEquals(finishedSpans.get(0).context().spanId(), finishedSpans.get(1).parentId());
    assertEquals("fooitem", finishedSpans.get(0).getBaggageItem("foobag"));
    assertNull(finishedSpans.get(0).getBaggageItem("barbag"));
    assertEquals("fooitem", finishedSpans.get(1).getBaggageItem("foobag"));
    assertEquals("baritem", finishedSpans.get(1).getBaggageItem("barbag"));
    assertEquals("donttouch", injectMap.get("foobag"));
  }

  @Test
  public void testTextMapPropagatorHttpHeaders() {
    MockTracer tracer = new MockTracer(MockTracer.Propagator.TEXT_MAP);
    {
      Span parentSpan = tracer.buildSpan("foo")
              .start();
      parentSpan.finish();

      HashMap<String, String> injectMap = new HashMap<>();
      tracer.inject(parentSpan.context(), Format.Builtin.HTTP_HEADERS,
              new TextMapAdapter(injectMap));

      SpanContext extract = tracer.extract(Format.Builtin.HTTP_HEADERS, new TextMapAdapter(injectMap));

      tracer.buildSpan("bar")
              .asChildOf(extract)
              .start()
              .finish();
    }
    List<MockSpan> finishedSpans = tracer.finishedSpans();

    assertEquals(2, finishedSpans.size());
    assertEquals(finishedSpans.get(0).context().traceId(), finishedSpans.get(1).context().traceId());
    assertEquals(finishedSpans.get(0).context().spanId(), finishedSpans.get(1).parentId());
  }

  @Test
  public void testBinaryPropagator() {
    MockTracer tracer = new MockTracer(MockTracer.Propagator.BINARY);
    {
      Span parentSpan = tracer.buildSpan("foo")
              .start();
      parentSpan.setBaggageItem("foobag", "fooitem");
      parentSpan.finish();

      ByteBuffer buffer = ByteBuffer.allocate(128);
      BinaryInject binary = BinaryAdapters.injectionCarrier(buffer);
      tracer.inject(parentSpan.context(), Format.Builtin.BINARY_INJECT, binary);

      buffer.rewind();
      SpanContext extract = tracer.extract(Format.Builtin.BINARY_EXTRACT, BinaryAdapters.extractionCarrier(buffer));

      Span childSpan = tracer.buildSpan("bar")
              .asChildOf(extract)
              .start();
      childSpan.setBaggageItem("barbag", "baritem");
      childSpan.finish();
    }
    List<MockSpan> finishedSpans = tracer.finishedSpans();

    assertEquals(2, finishedSpans.size());
    assertEquals(finishedSpans.get(0).context().traceId(), finishedSpans.get(1).context().traceId());
    assertEquals(finishedSpans.get(0).context().spanId(), finishedSpans.get(1).parentId());
    assertEquals("fooitem", finishedSpans.get(0).getBaggageItem("foobag"));
    assertNull(finishedSpans.get(0).getBaggageItem("barbag"));
    assertEquals("fooitem", finishedSpans.get(1).getBaggageItem("foobag"));
    assertEquals("baritem", finishedSpans.get(1).getBaggageItem("barbag"));
  }

//  @Test(expected = RuntimeException.class)
//  public void testBinaryPropagatorExtractError() {
//    MockTracer tracer = new MockTracer(MockTracer.Propagator.BINARY);
//    {
//      BinaryExtract binary = BinaryAdapters.extractionCarrier(ByteBuffer.allocate(4));
//      tracer.extract(Format.Builtin.BINARY_EXTRACT, binary);
//    }
//  }

  @Test
  public void testActiveSpan() {
    MockTracer mockTracer = new MockTracer();
    assertNull(mockTracer.activeSpan());

    Span span = mockTracer.buildSpan("foo").start();
    try (Scope scope = mockTracer.activateSpan(span)) {
      assertEquals(mockTracer.scopeManager().activeSpan(), mockTracer.activeSpan());
    }

    assertNull(mockTracer.activeSpan());
    assertTrue(mockTracer.finishedSpans().isEmpty());
  }

//  @Test
//  public void testActiveSpanFinish() {
//    MockTracer mockTracer = new MockTracer();
//    assertNull(mockTracer.activeSpan());
//
//    Scope scope = null;
//    try {
//      scope = mockTracer.buildSpan("foo").startActive(true);
//      assertEquals(mockTracer.scopeManager().activeSpan(), mockTracer.activeSpan());
//    } finally {
//      scope.close();
//    }
//
//    assertNull(mockTracer.activeSpan());
//    assertFalse(mockTracer.finishedSpans().isEmpty());
//  }

  @Test
  public void testReset() {
    MockTracer mockTracer = new MockTracer();

    mockTracer.buildSpan("foo")
            .start()
            .finish();

    assertEquals(1, mockTracer.finishedSpans().size());
    mockTracer.reset();
    assertEquals(0, mockTracer.finishedSpans().size());
  }

  @Test
  public void testFollowFromReference() {
    MockTracer tracer = new MockTracer(MockTracer.Propagator.TEXT_MAP);
    final MockSpan precedent = tracer.buildSpan("precedent").start();

    final MockSpan followingSpan = tracer.buildSpan("follows")
            .addReference(References.FOLLOWS_FROM, precedent.context())
            .start();

    assertEquals(precedent.context().spanId(), followingSpan.parentId());
    assertEquals(1, followingSpan.references().size());

    final MockSpan.Reference followsFromRef = followingSpan.references().get(0);

    assertEquals(new MockSpan.Reference(precedent.context(), References.FOLLOWS_FROM), followsFromRef);
  }

  @Test
  public void testMultiReferences() {
    MockTracer tracer = new MockTracer(MockTracer.Propagator.TEXT_MAP);
    final MockSpan parent = tracer.buildSpan("parent").start();
    final MockSpan precedent = tracer.buildSpan("precedent").start();

    final MockSpan followingSpan = tracer.buildSpan("follows")
            .addReference(References.FOLLOWS_FROM, precedent.context())
            .asChildOf(parent.context())
            .start();

    assertEquals(parent.context().spanId(), followingSpan.parentId());
    assertEquals(2, followingSpan.references().size());

    final MockSpan.Reference followsFromRef = followingSpan.references().get(0);
    final MockSpan.Reference parentRef = followingSpan.references().get(1);

    assertEquals(new MockSpan.Reference(precedent.context(), References.FOLLOWS_FROM), followsFromRef);
    assertEquals(new MockSpan.Reference(parent.context(), References.CHILD_OF), parentRef);
  }

  @Test
  public void testMultiReferencesBaggage() {
    MockTracer tracer = new MockTracer(MockTracer.Propagator.TEXT_MAP);
    final MockSpan parent = tracer.buildSpan("parent").start();
    parent.setBaggageItem("parent", "foo");
    final MockSpan precedent = tracer.buildSpan("precedent").start();
    precedent.setBaggageItem("precedent", "bar");

    final MockSpan followingSpan = tracer.buildSpan("follows")
            .addReference(References.FOLLOWS_FROM, precedent.context())
            .asChildOf(parent.context())
            .start();

    assertEquals("foo", followingSpan.getBaggageItem("parent"));
    assertEquals("bar", followingSpan.getBaggageItem("precedent"));
  }

  @Test
  public void testNonStandardReference() {
    MockTracer tracer = new MockTracer(MockTracer.Propagator.TEXT_MAP);
    final MockSpan parent = tracer.buildSpan("parent").start();

    final MockSpan nextSpan = tracer.buildSpan("follows")
            .addReference("a_reference", parent.context())
            .start();

    assertEquals(parent.context().spanId(), nextSpan.parentId());
    assertEquals(1, nextSpan.references().size());
    assertEquals(nextSpan.references().get(0),
            new MockSpan.Reference(parent.context(), "a_reference"));
  }

  @Test
  public void testTraceIdentifiers() {
    MockTracer mockTracer = new MockTracer();
    mockTracer.buildSpan("foo").start().finish();

    List<MockSpan> spans = mockTracer.finishedSpans();
    assertEquals(1, spans.size());

    MockSpan.MockContext context = spans.get(0).context();
    assertNotEquals(0, context.traceId());
    assertNotEquals(0, context.spanId());
    assertEquals(String.valueOf(context.traceId()), context.toTraceId());
    assertEquals(String.valueOf(context.spanId()), context.toSpanId());
  }

//  @Test
//  public void testDefaultConstructor() {
//    MockTracer mockTracer = new MockTracer();
//    Span span = mockTracer.buildSpan("foo").start();
//    Scope scope = mockTracer.activateSpan(span);
//    //assertEquals(scope, mockTracer.scopeManager().active());
//    assertEquals(span, mockTracer.scopeManager().activeSpan());
//
//    Map<String, String> propag = new HashMap<>();
//    mockTracer.inject(scope.span().context(), Format.Builtin.TEXT_MAP, new TextMapAdapter(propag));
//    assertFalse(propag.isEmpty());
//  }

  @Test
  public void testChildOfWithNullParentDoesNotThrowException() {
    MockTracer tracer = new MockTracer();
    final Span parent = null;
    Span span = tracer.buildSpan("foo").asChildOf(parent).start();
    span.finish();
  }

  @Test
  public void testClose() {
    MockTracer mockTracer = new MockTracer();
    mockTracer.buildSpan("foo").start().finish();

    mockTracer.close();
    assertEquals(0, mockTracer.finishedSpans().size());

    mockTracer.buildSpan("foo").start().finish();
    assertEquals(0, mockTracer.finishedSpans().size());
  }
}
