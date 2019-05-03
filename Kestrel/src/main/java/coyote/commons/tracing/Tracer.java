package coyote.commons.tracing;


import coyote.commons.tracing.propagation.Format;

import java.io.Closeable;

/**
 * Tracer is a simple, thin interface for Span creation and propagation across arbitrary transports.
 */
public interface Tracer extends Closeable {

  /**
   * @return the current {@link ScopeManager}, which may be a noop but may not be null.
   */
  ScopeManager scopeManager();

  /**
   * @return the active {@link Span}. This is a shorthand for {@code Tracer.scopeManager().activeSpan()}.
   */
  Span activeSpan();

  /**
   * Make a {@link Span} instance active for the current context (usually a thread).
   * This is a shorthand for {@code Tracer.scopeManager().activate(span)}.
   *
   * @param span span
   * @return a {@link Scope} instance to control the end of the active period for the {@link Span}. It is a
   * programming error to neglect to call {@link Scope#close()} on the returned instance,
   * and it may lead to memory leaks as the {@link Scope} may remain in the thread-local stack.
   */
  Scope activateSpan(Span span);

  /**
   * Return a new SpanBuilder for a Span with the given `operationName`.
   *
   * <p>You can override the operationName later via {@link Span#setOperationName(String)}.
   *
   * <p>A contrived example:
   * <pre><code>
   *   Tracer tracer = ...
   *
   *   // Note: if there is a `tracer.activeSpan()` instance, it will be used as the target
   *   // of an implicit CHILD_OF Reference when `start()` is invoked,
   *   // unless another Span reference is explicitly provided to the builder.
   *   Span span = tracer.buildSpan("HandleHTTPRequest")
   *                     .asChildOf(rpcSpanContext)  // an explicit parent
   *                     .withTag("user_agent", req.UserAgent)
   *                     .withTag("lucky_number", 42)
   *                     .start();
   *   span.setTag("...", "...");
   *
   *   // It is possible to set the Span as the active instance for the current context
   *   // (usually a thread).
   *   try (Scope scope = tracer.activateSpan(span)) {
   *      ...
   *   }
   * </code></pre>
   * @param operationName  name
   * @return  the builder
   */
  SpanBuilder buildSpan(String operationName);

  /**
   * Inject a SpanContext into a `carrier` of a given type, presumably for propagation across process boundaries.
   *
   * <p>Example:
   * <pre><code>
   * Tracer tracer = ...
   * Span clientSpan = ...
   * TextMap httpHeadersCarrier = new AnHttpHeaderCarrier(httpRequest);
   * tracer.inject(span.context(), Format.Builtin.HTTP_HEADERS, httpHeadersCarrier);
   * </code></pre>
   *
   * @param <C>         the carrier type, which also parametrizes the Format.
   * @param spanContext the SpanContext instance to inject into the carrier
   * @param format      the Format of the carrier
   * @param carrier     the carrier for the SpanContext state. All Tracer.inject() implementations must support
   *                    io.opentracing.propagation.TextMap and java.nio.ByteBuffer.
   */
  <C> void inject(SpanContext spanContext, Format<C> format, C carrier);

  /**
   * Extract a SpanContext from a `carrier` of a given type, presumably after propagation across a process boundary.
   *
   * <p>Example:
   * <pre><code>
   * Tracer tracer = ...
   * TextMap httpHeadersCarrier = new AnHttpHeaderCarrier(httpRequest);
   * SpanContext spanCtx = tracer.extract(Format.Builtin.HTTP_HEADERS, httpHeadersCarrier);
   * ... = tracer.buildSpan('...').asChildOf(spanCtx).start();
   * </code></pre>
   * <p>
   * If the span serialized state is invalid (corrupt, wrong version, etc) inside the carrier this will result in an
   * IllegalArgumentException. If the span serialized state is missing the method returns null.
   *
   * @param <C>     the carrier type, which also parametrizes the Format.
   * @param format  the Format of the carrier
   * @param carrier the carrier for the SpanContext state. All Tracer.extract() implementations must support
   *                io.opentracing.propagation.TextMap and java.nio.ByteBuffer.
   * @return the SpanContext instance holding context to create a Span, null otherwise.
   */
  <C> SpanContext extract(Format<C> format, C carrier);

  /**
   * Closes the Tracer, and tries to flush the in-memory collection to the configured persistance store.
   *
   * <p>
   * The close method should be considered idempotent; closing an already closed Tracer should not raise an error.
   * Spans that are created or finished after a Tracer has been closed may or may not be flushed.
   * Calling the close method should be considered a synchronous operation. Observe this call may block for
   * a relatively long period of time, depending on the internal shutdown.
   * <p>
   * For stateless tracers, this can be a no-op.
   */
  @Override
  void close();

  interface SpanBuilder {

    /**
     * A shorthand for addReference(References.CHILD_OF, parent).
     *
     * <p>
     * If parent==null, this is a noop.
     * @param parent parent
     * @return builder
     */
    SpanBuilder asChildOf(SpanContext parent);

    /**
     * A shorthand for addReference(References.CHILD_OF, parent.context()).
     *
     * <p>
     * If parent==null, this is a noop.
     *
     * @param parent parent
     * @return builder
     */
    SpanBuilder asChildOf(Span parent);

    /**
     * Add a reference from the Span being built to a distinct (usually parent) Span. May be called multiple times
     * to represent multiple such References.
     *
     * <p>
     * If
     * <ul>
     * <li>the {@link Tracer}'s {@link ScopeManager#activeSpan()} is not null, and
     * <li>no <b>explicit</b> references are added via {@link SpanBuilder#addReference}, and
     * <li>{@link SpanBuilder#ignoreActiveSpan()} is not invoked,
     * </ul>
     * ... then an inferred {@link References#CHILD_OF} reference is created to the
     * {@link ScopeManager#activeSpan()} {@link SpanContext} when {@link SpanBuilder#start} is invoked.
     *
     * @param referenceType     the reference type, typically one of the constants defined in References
     * @param referencedContext the SpanContext being referenced; e.g., for a References.CHILD_OF referenceType, the
     *                          referencedContext is the parent. If referencedContext==null, the call to
     *                          {@link #addReference} is a noop.
     * @return builder
     * @see References
     */
    SpanBuilder addReference(String referenceType, SpanContext referencedContext);

    /**
     * Do not create an implicit {@link References#CHILD_OF} reference to the {@link ScopeManager#activeSpan()}).
     * @return builder
     */
    SpanBuilder ignoreActiveSpan();

    /**
     * Same as {@link Span#setTag(String, String)}, but for the span being built.
     * @param key key
     * @param value value
     * @return builder
     */
    SpanBuilder withTag(String key, String value);

    /**
     * Same as {@link Span#setTag(String, boolean)}, but for the span being built.
     * @param key key
     * @param value value
     * @return builder
     */
    SpanBuilder withTag(String key, boolean value);

    /**
     * Same as {@link Span#setTag(String, Number)}, but for the span being built.
     * @param key key
     * @param value value
     * @return builder
     */
    SpanBuilder withTag(String key, Number value);

    /**
     * Same as  AbstractTag#set(Span, T), but for the span being built.
     * @param tag tag
     * @param value value
     * @param <T> type
     * @return builder
     */
    <T> SpanBuilder withTag(Tag<T> tag, T value);

    /**
     * Specify a timestamp of when the Span was started, represented in microseconds since epoch.
     * @param microseconds  msecs
     * @return builder
     */
    SpanBuilder withStartTimestamp(long microseconds);


    /**
     * Returns a newly-started {@link Span}.
     *
     * <p>
     * If
     * <ul>
     * <li>the {@link Tracer}'s {@link ScopeManager#activeSpan()} is not null, and
     * <li>no <b>explicit</b> references are added via {@link SpanBuilder#addReference}, and
     * <li>{@link SpanBuilder#ignoreActiveSpan()} is not invoked,
     * </ul>
     * ... then an inferred {@link References#CHILD_OF} reference is created to the
     * {@link ScopeManager#activeSpan()}'s {@link SpanContext} when
     * {@link SpanBuilder#start()} is invoked.
     *
     * @return the newly-started Span instance, which has *not* been automatically registered
     * via the {@link ScopeManager}
     */
    Span start();


  }
}