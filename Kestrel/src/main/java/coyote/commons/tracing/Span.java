package coyote.commons.tracing;

import java.util.Map;


public interface Span {
  /**
   * Retrieve the associated SpanContext.
   * <p>
   * This may be called at any time, including after calls to finish().
   *
   * @return the SpanContext that encapsulates Span state that should propagate across process boundaries.
   */
  SpanContext context();

  /**
   * Set a key:value tag on the Span.
   * @param key key
   * @param value value
   * @return the span for chaining
   */
  Span setTag(String key, String value);

  /**
   * Same as {@link #setTag(String, String)}, but for boolean values.
   * @param key key
   * @param value value
   * @return the span for chaining
   */
  Span setTag(String key, boolean value);

  /**
   * Same as {@link #setTag(String, String)}, but for numeric values.
   * @param key key
   * @param value value
   * @return the span for chaining
   */
  Span setTag(String key, Number value);

  /**
   * Same as {@link #setTag(String, String)}, but with using Tag&lt;T&gt;.
   * @param tag key
   * @param value value
   * @param <T> type
   * @return the span for chaining
   */
  <T> Span setTag(Tag<T> tag, T value);

  /**
   * Log key:value pairs to the Span with the current walltime timestamp.
   *
   * <p><strong>CAUTIONARY NOTE:</strong> not all Tracer implementations support key:value log fields end-to-end.
   * Caveat emptor.
   *
   * @param fields key:value log fields. Tracer implementations should support String, numeric, and boolean values;
   *               some may also support arbitrary Objects.
   * @return the Span, for chaining
   */
  Span log(Map<String, ?> fields);

  /**
   * Like log(Map&lt;String, Object&gt;), but with an explicit timestamp.
   *
   * <p><strong>CAUTIONARY NOTE:</strong> not all Tracer implementations support key:value log fields end-to-end.
   * Caveat emptor.
   *
   * @param timestampMicroseconds The explicit timestamp for the log record. Must be greater than or equal to the
   *                              Span's start timestamp.
   * @param fields                key:value log fields. Tracer implementations should support String, numeric, and boolean values;
   *                              some may also support arbitrary Objects.
   * @return the Span, for chaining
   * @see Span#log(long, String)
   */
  Span log(long timestampMicroseconds, Map<String, ?> fields);

  /**
   * Record an event at the current walltime timestamp.
   * <p>
   * Shorthand for
   *
   * <pre><code>
   * span.log(Collections.singletonMap("event", event));
   * </code></pre>
   *
   * @param event the event value; often a stable identifier for a moment in the Span lifecycle
   * @return the Span, for chaining
   */
  Span log(String event);

  /**
   * Record an event at a specific timestamp.
   * <p>
   * Shorthand for
   *
   * <pre><code>
   * span.log(timestampMicroseconds, Collections.singletonMap("event", event));
   * </code></pre>
   *
   * @param timestampMicroseconds The explicit timestamp for the log record. Must be greater than or equal to the
   *                              Span's start timestamp.
   * @param event                 the event value; often a stable identifier for a moment in the Span lifecycle
   * @return the Span, for chaining
   */
  Span log(long timestampMicroseconds, String event);

  /**
   * Sets a baggage item in the Span (and its SpanContext) as a key/value pair.
   * <p>
   * Baggage enables powerful distributed context propagation functionality where arbitrary application data can be
   * carried along the full path of request execution throughout the system.
   * <p>
   * Note 1: Baggage is only propagated to the future (recursive) children of this SpanContext.
   * <p>
   * Note 2: Baggage is sent in-band with every subsequent local and remote calls, so this feature must be used with
   * care.
   *
   * @param key key
   * @param value value
   * @return this Span instance, for chaining
   */
  Span setBaggageItem(String key, String value);

  /**
   * @param key key
   * @return the value of the baggage item identified by the given key, or null if no such item could be found
   */
  String getBaggageItem(String key);

  /**
   * Sets the string name for the logical operation this span represents.
   *
   * @param operationName  name
   * @return this Span instance, for chaining
   */
  Span setOperationName(String operationName);

  /**
   * Sets the end timestamp to now and records the span.
   *
   * <p>With the exception of calls to {@link #context}, this should be the last call made to the span instance.
   * Future calls to {@link #finish} are defined as noops, and future calls to methods other than {@link #context}
   * lead to undefined behavior.
   *
   * @see Span#context()
   */
  void finish();

  /**
   * Sets an explicit end timestamp and records the span.
   *
   * <p>With the exception of calls to Span.context(), this should be the last call made to the span instance, and to
   * do otherwise leads to undefined behavior.
   *
   * @param finishMicros an explicit finish time, in microseconds since the epoch
   * @see Span#context()
   */
  void finish(long finishMicros);
}
