package coyote.commons.tracing;


import java.io.Closeable;

/**
 * A {@link Scope} formalizes the activation and deactivation of a {@link Span}, usually from a CPU standpoint.
 *
 * <p>
 * Many times a {@link Span} will be extant (in that {@link Span#finish()} has not been called) despite being in a
 * non-runnable state from a CPU/scheduler standpoint. For instance, a {@link Span} representing the client side of an
 * RPC will be unfinished but blocked on IO while the RPC is still outstanding. A {@link Scope} defines when a given
 * {@link Span} <em>is</em> scheduled and on the path.
 */
public interface Scope extends Closeable {
  /**
   * Mark the end of the active period for the current context (usually a thread)
   * and {@link Scope}, updating {@link ScopeManager#active()} and {@link ScopeManager#activeSpan()}
   * in the process.
   *
   * <p>
   * NOTE: Calling {@link #close} more than once on a single {@link Scope} instance leads to undefined
   * behavior.
   */
  @Override
  void close();

  /**
   * @deprecated use {@link Span} directly or access it through {@link ScopeManager#activeSpan()}
   * Return the corresponding active {@link Span} for this instance.
   *
   * @return the {@link Span} that's been scoped by this {@link Scope}
   */
  @Deprecated
  Span span();
}