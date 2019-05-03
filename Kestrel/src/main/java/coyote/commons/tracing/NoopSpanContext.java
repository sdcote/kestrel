package coyote.commons.tracing;


import java.util.Collections;
import java.util.Map;


public interface NoopSpanContext extends SpanContext {
}

final class NoopSpanContextImpl implements NoopSpanContext {
  static final NoopSpanContextImpl INSTANCE = new NoopSpanContextImpl();

  @Override
  public String toTraceId() {
    return "";
  }

  @Override
  public String toSpanId() {
    return "";
  }

  @Override
  public Iterable<Map.Entry<String, String>> baggageItems() {
    return Collections.emptyList();
  }

  @Override
  public String toString() {
    return NoopSpanContext.class.getSimpleName();
  }

}
