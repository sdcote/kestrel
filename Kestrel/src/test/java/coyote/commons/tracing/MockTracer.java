package coyote.commons.tracing;

import coyote.commons.tracing.propagation.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * MockTracer makes it easy to test the semantics of OpenTracing instrumentation.
 * <p>
 * By using a MockTracer as an io.opentracing.Tracer implementation for unittests, a developer can assert that Span
 * properties and relationships with other Spans are defined as expected by instrumentation code.
 * <p>
 * The MockTracerTest has simple usage examples.
 */
public class MockTracer implements Tracer {
  private final List<MockSpan> finishedSpans = new ArrayList<>();
  private final Propagator propagator;
  private final ScopeManager scopeManager;
  private boolean isClosed;

  public MockTracer() {
    this(new ThreadLocalScopeManager(), Propagator.TEXT_MAP);
  }

  public MockTracer(ScopeManager scopeManager) {
    this(scopeManager, Propagator.TEXT_MAP);
  }

  public MockTracer(ScopeManager scopeManager, Propagator propagator) {
    this.scopeManager = scopeManager;
    this.propagator = propagator;
  }

  /**
   * Create a new MockTracer that passes through any calls to inject() and/or extract().
   */
  public MockTracer(Propagator propagator) {
    this(new ThreadLocalScopeManager(), propagator);
  }

  /**
   * Clear the finishedSpans() queue.
   * <p>
   * Note that this does *not* have any effect on Spans created by MockTracer that have not finish()ed yet; those
   * will still be enqueued in finishedSpans() when they finish().
   */
  public synchronized void reset() {
    this.finishedSpans.clear();
  }

  /**
   * @return a copy of all finish()ed MockSpans started by this MockTracer (since construction or the last call to
   * MockTracer.reset()).
   * @see MockTracer#reset()
   */
  public synchronized List<MockSpan> finishedSpans() {
    return new ArrayList<>(this.finishedSpans);
  }

  /**
   * Noop method called on {@link Span#finish()}.
   */
  protected void onSpanFinished(MockSpan mockSpan) {
  }

  @Override
  public ScopeManager scopeManager() {
    return this.scopeManager;
  }

  @Override
  public SpanBuilder buildSpan(String operationName) {
    return new SpanBuilder(operationName);
  }

  @Override
  public <C> void inject(SpanContext spanContext, Format<C> format, C carrier) {
    this.propagator.inject((MockSpan.MockContext) spanContext, format, carrier);
  }

  @Override
  public <C> SpanContext extract(Format<C> format, C carrier) {
    return this.propagator.extract(format, carrier);
  }

  @Override
  public Span activeSpan() {
    return this.scopeManager.activeSpan();
  }

  @Override
  public Scope activateSpan(Span span) {
    return this.scopeManager.activate(span);
  }

  @Override
  public synchronized void close() {
    this.isClosed = true;
    this.finishedSpans.clear();
  }

  synchronized void appendFinishedSpan(MockSpan mockSpan) {
    if (isClosed)
      return;

    this.finishedSpans.add(mockSpan);
    this.onSpanFinished(mockSpan);
  }

  private SpanContext activeSpanContext() {
    Span span = activeSpan();
    if (span == null) {
      return null;
    }

    return span.context();
  }

  /**
   * Propagator allows the developer to intercept and verify any calls to inject() and/or extract().
   * <p>
   * By default, MockTracer uses Propagator.PRINTER which simply logs such calls to System.out.
   *
   * @see MockTracer#MockTracer(Propagator)
   */
  public interface Propagator {
    Propagator PRINTER = new Propagator() {
      @Override
      public <C> void inject(MockSpan.MockContext ctx, Format<C> format, C carrier) {
        System.out.println("inject(" + ctx + ", " + format + ", " + carrier + ")");
      }

      @Override
      public <C> MockSpan.MockContext extract(Format<C> format, C carrier) {
        System.out.println("extract(" + format + ", " + carrier + ")");
        return null;
      }
    };
    Propagator BINARY = new Propagator() {
      static final int BUFFER_SIZE = 128;

      @Override
      public <C> void inject(MockSpan.MockContext ctx, Format<C> format, C carrier) {
        if (!(carrier instanceof BinaryInject)) {
          throw new IllegalArgumentException("Expected BinaryInject, received " + carrier.getClass());
        }

        BinaryInject binary = (BinaryInject) carrier;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ObjectOutputStream objStream = null;
        try {
          objStream = new ObjectOutputStream(stream);
          objStream.writeLong(ctx.spanId());
          objStream.writeLong(ctx.traceId());

          for (Map.Entry<String, String> entry : ctx.baggageItems()) {
            objStream.writeUTF(entry.getKey());
            objStream.writeUTF(entry.getValue());
          }
          objStream.flush(); // *need* to flush ObjectOutputStream.

          byte[] buff = stream.toByteArray();
          binary.injectionBuffer(buff.length).put(buff);

        } catch (IOException e) {
          throw new RuntimeException("Corrupted state", e);
        } finally {
          if (objStream != null) {
            try {
              objStream.close();
            } catch (Exception e2) {
            }
          }
        }
      }

      @Override
      public <C> MockSpan.MockContext extract(Format<C> format, C carrier) {
        if (!(carrier instanceof BinaryExtract)) {
          throw new IllegalArgumentException("Expected BinaryExtract, received " + carrier.getClass());
        }

        Long traceId = null;
        Long spanId = null;
        Map<String, String> baggage = new HashMap<>();

        BinaryExtract binary = (BinaryExtract) carrier;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectInputStream objStream = null;
        try {
          ByteBuffer extractBuff = binary.extractionBuffer();
          byte[] buff = new byte[extractBuff.remaining()];
          extractBuff.get(buff);

          objStream = new ObjectInputStream(new ByteArrayInputStream(buff));
          spanId = objStream.readLong();
          traceId = objStream.readLong();

          while (objStream.available() > 0) {
            baggage.put(objStream.readUTF(), objStream.readUTF());
          }
        } catch (IOException e) {
          throw new RuntimeException("Corrupted state", e);
        } finally {
          if (objStream != null) {
            try {
              objStream.close();
            } catch (Exception e2) {
            }
          }
        }

        if (traceId != null && spanId != null) {
          return new MockSpan.MockContext(traceId, spanId, baggage);
        }

        return null;
      }
    };
    Propagator TEXT_MAP = new Propagator() {
      public static final String SPAN_ID_KEY = "spanid";
      public static final String TRACE_ID_KEY = "traceid";
      public static final String BAGGAGE_KEY_PREFIX = "baggage-";

      @Override
      public <C> void inject(MockSpan.MockContext ctx, Format<C> format, C carrier) {
        if (carrier instanceof TextMapInject) {
          TextMapInject textMap = (TextMapInject) carrier;
          for (Map.Entry<String, String> entry : ctx.baggageItems()) {
            textMap.put(BAGGAGE_KEY_PREFIX + entry.getKey(), entry.getValue());
          }
          textMap.put(SPAN_ID_KEY, String.valueOf(ctx.spanId()));
          textMap.put(TRACE_ID_KEY, String.valueOf(ctx.traceId()));
        } else {
          throw new IllegalArgumentException("Unknown carrier");
        }
      }

      @Override
      public <C> MockSpan.MockContext extract(Format<C> format, C carrier) {
        Long traceId = null;
        Long spanId = null;
        Map<String, String> baggage = new HashMap<>();

        if (carrier instanceof TextMapExtract) {
          TextMapExtract textMap = (TextMapExtract) carrier;
          for (Map.Entry<String, String> entry : textMap) {
            if (TRACE_ID_KEY.equals(entry.getKey())) {
              traceId = Long.valueOf(entry.getValue());
            } else if (SPAN_ID_KEY.equals(entry.getKey())) {
              spanId = Long.valueOf(entry.getValue());
            } else if (entry.getKey().startsWith(BAGGAGE_KEY_PREFIX)) {
              String key = entry.getKey().substring((BAGGAGE_KEY_PREFIX.length()));
              baggage.put(key, entry.getValue());
            }
          }
        } else {
          throw new IllegalArgumentException("Unknown carrier");
        }

        if (traceId != null && spanId != null) {
          return new MockSpan.MockContext(traceId, spanId, baggage);
        }

        return null;
      }
    };

    <C> void inject(MockSpan.MockContext ctx, Format<C> format, C carrier);

    <C> MockSpan.MockContext extract(Format<C> format, C carrier);
  }

  public final class SpanBuilder implements Tracer.SpanBuilder {
    private final String operationName;
    private long startMicros;
    private List<MockSpan.Reference> references = new ArrayList<>();
    private boolean ignoringActiveSpan;
    private Map<String, Object> initialTags = new HashMap<>();

    SpanBuilder(String operationName) {
      this.operationName = operationName;
    }

    @Override
    public SpanBuilder asChildOf(SpanContext parent) {
      return addReference(References.CHILD_OF, parent);
    }

    @Override
    public SpanBuilder asChildOf(Span parent) {
      if (parent == null) {
        return this;
      }
      return addReference(References.CHILD_OF, parent.context());
    }

    @Override
    public SpanBuilder ignoreActiveSpan() {
      ignoringActiveSpan = true;
      return this;
    }

    @Override
    public SpanBuilder addReference(String referenceType, SpanContext referencedContext) {
      if (referencedContext != null) {
        this.references.add(new MockSpan.Reference((MockSpan.MockContext) referencedContext, referenceType));
      }
      return this;
    }

    @Override
    public SpanBuilder withTag(String key, String value) {
      this.initialTags.put(key, value);
      return this;
    }

    @Override
    public SpanBuilder withTag(String key, boolean value) {
      this.initialTags.put(key, value);
      return this;
    }

    @Override
    public SpanBuilder withTag(String key, Number value) {
      this.initialTags.put(key, value);
      return this;
    }

    @Override
    public <T> Tracer.SpanBuilder withTag(Tag<T> tag, T value) {
      this.initialTags.put(tag.getKey(), value);
      return this;
    }

    @Override
    public SpanBuilder withStartTimestamp(long microseconds) {
      this.startMicros = microseconds;
      return this;
    }

    @Override
    public MockSpan start() {
      if (this.startMicros == 0) {
        this.startMicros = MockSpan.nowMicros();
      }
      SpanContext activeSpanContext = activeSpanContext();
      if (references.isEmpty() && !ignoringActiveSpan && activeSpanContext != null) {
        references.add(new MockSpan.Reference((MockSpan.MockContext) activeSpanContext, References.CHILD_OF));
      }
      return new MockSpan(MockTracer.this, operationName, startMicros, initialTags, references);
    }
  }
}