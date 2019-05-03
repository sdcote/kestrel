package coyote.commons.tracing;

import java.util.Map;

public interface SpanContext {

  /**
   * Return the ID of the trace.
   *
   * Should be globally unique. Every span in a trace shares this ID.
   *
   * An empty String will be returned if the tracer does not support this functionality
   * (this is the case for no-op tracers, for example). null is an invalid return value.
   *
   * @return the trace ID for this context.
   */
  String toTraceId();

  /**
   * Return the ID of the associated Span.
   *
   * Should be unique within a trace. Each span within a trace contains a different ID.
   *
   * An empty String will be returned if the tracer does not support this functionality
   * (this is the case for no-op tracers, for example). null is an invalid return value.
   *
   * @return the Span ID for this context.
   */
  String toSpanId();

  /**
   * @return all zero or more baggage items propagating along with the associated Span
   *
   * @see Span#setBaggageItem(String, String)
   * @see Span#getBaggageItem(String)
   */
  Iterable<Map.Entry<String, String>> baggageItems();


}
