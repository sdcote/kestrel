package coyote.commons.tracing;



/**
 * {@link ThreadLocalScope} is a simple {@link Scope} implementation that relies on Java's
 * thread-local storage primitive.
 *
 * @see ScopeManager
 */
public class ThreadLocalScope implements Scope {
  private final ThreadLocalScopeManager scopeManager;
  private final Span wrapped;
  private final boolean finishOnClose;
  private final ThreadLocalScope toRestore;

  ThreadLocalScope(ThreadLocalScopeManager scopeManager, Span wrapped) {
    this(scopeManager, wrapped, false);
  }

  ThreadLocalScope(ThreadLocalScopeManager scopeManager, Span wrapped, boolean finishOnClose) {
    this.scopeManager = scopeManager;
    this.wrapped = wrapped;
    this.finishOnClose = finishOnClose;
    this.toRestore = scopeManager.tlsScope.get();
    scopeManager.tlsScope.set(this);
  }

  @Override
  public void close() {
    if (scopeManager.tlsScope.get() != this) {
      // This shouldn't happen if users call methods in the expected order. Bail out.
      return;
    }

    if (finishOnClose) {
      wrapped.finish();
    }

    scopeManager.tlsScope.set(toRestore);
  }


  public Span span() {
    return wrapped;
  }
}