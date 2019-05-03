package coyote.commons.tracing;


import coyote.commons.tracing.propagation.Format;

public interface NoopTracer extends Tracer {
}

final class NoopTracerImpl implements NoopTracer {
  final static NoopTracer INSTANCE = new NoopTracerImpl();

  @Override
  public ScopeManager scopeManager() {
    return NoopScopeManager.INSTANCE;
  }

  @Override
  public Span activeSpan() {
    return NoopSpanImpl.INSTANCE;
  }

  @Override
  public Scope activateSpan(Span span) {
    return NoopScopeManager.NoopScope.INSTANCE;
  }

  @Override
  public SpanBuilder buildSpan(String operationName) {
    return NoopSpanBuilderImpl.INSTANCE;
  }

  @Override
  public <C> void inject(SpanContext spanContext, Format<C> format, C carrier) {
  }

  @Override
  public <C> SpanContext extract(Format<C> format, C carrier) {
    return NoopSpanContextImpl.INSTANCE;
  }

  @Override
  public void close() {
  }

  @Override
  public String toString() {
    return NoopTracer.class.getSimpleName();
  }
}

