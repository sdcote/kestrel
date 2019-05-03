package coyote.commons.tracing;


/**
 * The {@link ScopeManager} interface abstracts both the activation of {@link Span} instances via
 * {@link ScopeManager#activate(Span)} and access to an active {@link Span}
 * via {@link ScopeManager#activeSpan()}.
 *
 * @see Scope
 * @see Tracer#scopeManager()
 */
public interface ScopeManager {

  /**
   * Set the specified {@link Span} as the active instance for the current
   * context (usually a thread).
   *
   * <p>
   * The returned {@link Scope} represents the active state for the span.
   * Once its active period is due, {@link Scope#close()} ought to be called.
   * To ease this operation, {@link Scope} supports try-with-resources.
   * Observe the span will not be automatically finished when {@link Scope#close()}
   * is called.
   *
   * <p>
   * The corresponding {@link Span} can be accessed at any time through {@link #activeSpan()}.
   *
   * <p>
   * Usage:
   * <pre><code>
   *     Span span = tracer.buildSpan("...").start();
   *     try (Scope scope = tracer.scopeManager().activate(span)) {
   *         span.setTag("...", "...");
   *         ...
   *     } catch (Exception e) {
   *         span.log(...);
   *     } finally {
   *         // Optionally finish the Span if the operation it represents
   *         // is logically completed at this point.
   *         span.finish();
   *     }
   * </code></pre>
   *
   * @param span the {@link Span} that should become the {@link #activeSpan()}
   * @return a {@link Scope} instance to control the end of the active period for the {@link Span}. It is a
   * programming error to neglect to call {@link Scope#close()} on the returned instance.
   */
  Scope activate(Span span);

  /**
   * @return the {@link Scope active scope}, or null if none could be found.
   * @deprecated use {@link #activeSpan()} instead.
   * Return the currently active {@link Scope} which can be used to deactivate the currently active
   * {@link Span}.
   *
   * <p>
   * Observe that {@link Scope} is expected to be used only in the same thread where it was
   * created, and thus should not be passed across threads.
   *
   * <p>
   * Because both {@link #active()} and {@link #activeSpan()} reference the current
   * active state, they both will be either null or non-null.
   */
  @Deprecated
  Scope active();

  /**
   * Return the currently active {@link Span}.
   *
   * <p>
   * Because both {@link #active()} and {@link #activeSpan()} reference the current
   * active state, they both will be either null or non-null.
   *
   * @return the {@link Span active span}, or null if none could be found.
   */
  Span activeSpan();

  /**
   * @param span              the {@link Span} that should become the {@link #activeSpan()}
   * @param finishSpanOnClose whether span should automatically be finished when {@link Scope#close()} is called
   * @return a {@link Scope} instance to control the end of the active period for the {@link Span}. It is a
   * programming error to neglect to call {@link Scope#close()} on the returned instance.
   * @deprecated use {@link #activate(Span)} instead.
   * Set the specified {@link Span} as the active instance for the current
   * context (usually a thread).
   *
   * <p>
   * Finishing the {@link Span} upon {@link Scope#close()} is discouraged,
   * as reporting errors becomes impossible:
   * <pre><code>
   *     try (Scope scope = tracer.scopeManager().activate(span, true)) {
   *     } catch (Exception e) {
   *         // Not possible to report errors, as
   *         // the span has been already finished.
   *     }
   * </code></pre>
   */
  @Deprecated
  Scope activate(Span span, boolean finishSpanOnClose);
}