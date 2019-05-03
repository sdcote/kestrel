package coyote.commons.tracing;


public interface NoopSpanBuilder extends Tracer.SpanBuilder {
  NoopSpanBuilder INSTANCE = new NoopSpanBuilderImpl();
}

final class NoopSpanBuilderImpl implements NoopSpanBuilder {

  @Override
  public Tracer.SpanBuilder addReference(String refType, SpanContext referenced) {
    return this;
  }

  @Override
  public Tracer.SpanBuilder asChildOf(SpanContext parent) {
    return this;
  }

  @Override
  public Tracer.SpanBuilder ignoreActiveSpan() {
    return this;
  }

  @Override
  public Tracer.SpanBuilder asChildOf(Span parent) {
    return this;
  }

  @Override
  public Tracer.SpanBuilder withTag(String key, String value) {
    return this;
  }

  @Override
  public Tracer.SpanBuilder withTag(String key, boolean value) {
    return this;
  }

  @Override
  public Tracer.SpanBuilder withTag(String key, Number value) {
    return this;
  }

  @Override
  public <T> Tracer.SpanBuilder withTag(Tag<T> key, T value) {
    return this;
  }

  @Override
  public Tracer.SpanBuilder withStartTimestamp(long microseconds) {
    return this;
  }

  @Override
  public Span start() { return NoopSpanImpl.INSTANCE; }

  @Override
  public String toString() { return NoopSpanBuilder.class.getSimpleName(); }
}
