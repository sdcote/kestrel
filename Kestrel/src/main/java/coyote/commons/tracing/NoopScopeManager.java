package coyote.commons.tracing;


public interface NoopScopeManager extends ScopeManager {
  NoopScopeManager INSTANCE = new NoopScopeManagerImpl();

  interface NoopScope extends Scope {
    NoopScope INSTANCE = new NoopScopeManagerImpl.NoopScopeImpl();
  }
}

/**
 * A noop (i.e., cheap-as-possible) implementation of an ScopeManager.
 */
class NoopScopeManagerImpl implements NoopScopeManager {

  @Override
  public Scope activate(Span span) {
    return NoopScope.INSTANCE;
  }


  @Override
  public Span activeSpan() {
    return NoopSpan.INSTANCE;
  }

  static class NoopScopeImpl implements NoopScopeManager.NoopScope {
    @Override
    public void close() {
    }

  }
}
