package coyote.commons.tracing;



/**
 * A simple {@link ScopeManager} implementation built on top of Java's thread-local storage primitive.
 *
 * @see ThreadLocalScope
 */
public class ThreadLocalScopeManager implements ScopeManager {
  final ThreadLocal<ThreadLocalScope> tlsScope = new ThreadLocal<ThreadLocalScope>();

  @Override
  public Scope activate(Span span) {
    return new ThreadLocalScope(this, span);
  }


  @Override
  public Span activeSpan() {
    Scope scope = tlsScope.get();
    return scope == null ? null : ((ThreadLocalScope)scope).span();
  }
}
