package coyote.commons.tracing;


import coyote.commons.tracing.propagation.Format;

import java.util.concurrent.Callable;

/**
 * Global tracer that forwards all methods to another tracer that can be
 * configured by calling {@link #registerIfAbsent(Tracer) register}.
 *
 * <p>
 * The {@linkplain #registerIfAbsent(Tracer) register} method should only be called once
 * during the application initialization phase.<br>
 * If the {@linkplain #registerIfAbsent(Tracer)} register} method is never called,
 * the default {@link NoopTracer} is used.
 *
 * <p>
 * Where possible, use some form of dependency injection (of which there are
 * many) to access the `Tracer` instance. For vanilla application code, this is
 * often reasonable and cleaner for all of the usual DI reasons.
 *
 * <p>
 * That said, instrumentation for packages that are themselves statically
 * configured (e.g., JDBC drivers) may be unable to make use of said DI
 * mechanisms for {@link Tracer} access, and as such they should fall back on
 * {@link GlobalTracer}. By and large, OpenTracing instrumentation should
 * always allow the programmer to specify a {@link Tracer} instance to use for
 * instrumentation, though the {@link GlobalTracer} is a reasonable fallback or
 * default value.
 */
public final class GlobalTracer implements Tracer {

  /**
   * Singleton instance.
   * <p>
   * Since we cannot prevent people using {@linkplain #get() GlobalTracer.get()} as a constant,
   * this guarantees that references obtained before, during or after initialization
   * all behave as if obtained <em>after</em> initialization once properly initialized.<br>
   * As a minor additional benefit it makes it harder to circumvent the {@link Tracer} API.
   */
  private static final GlobalTracer INSTANCE = new GlobalTracer();

  /**
   * The registered {@link Tracer} delegate or the {@link NoopTracer} if none was registered yet.
   * Never {@code null}.
   */
  private static volatile Tracer tracer = NoopTracerFactory.create();

  private static volatile boolean isRegistered = false;

  private GlobalTracer() {
  }

  /**
   * Returns the constant {@linkplain GlobalTracer}.
   * <p>
   * All methods are forwarded to the currently configured tracer.<br>
   * Until a tracer is {@link #registerIfAbsent(Tracer) explicitly configured},
   * the {@link NoopTracer NoopTracer} is used.
   *
   * @return The global tracer constant.
   * @see #registerIfAbsent(Tracer) and {@link #registerIfAbsent(Callable)}
   */
  public static Tracer get() {
    return INSTANCE;
  }

  /**
   * Identify whether a {@link Tracer} has previously been registered.
   * <p>
   * This check is useful in scenarios where more than one component may be responsible
   * for registering a tracer. For example, when using a Java Agent, it will need to determine
   * if the application has already registered a tracer, and if not attempt to resolve and
   * register one itself.
   *
   * @return Whether a tracer has been registered
   */
  public static boolean isRegistered() {
    return isRegistered;
  }

  /**
   * Register a {@link Tracer} to back the behaviour of the {@link #get()}.
   * <p>
   * The tracer is provided through a {@linkplain Callable} that will only be called if the global tracer is absent.
   * Registration is a one-time operation. Once a tracer has been registered, all attempts at re-registering
   * will return {@code false}.
   * <p>
   * Every application intending to use the global tracer is responsible for registering it once
   * during its initialization.
   *
   * @param provider Provider for the tracer to use as global tracer.
   * @return {@code true} if the provided tracer was registered as a result of this call,
   * {@code false} otherwise.
   * @throws NullPointerException if the tracer provider is {@code null} or provides a {@code null} Tracer.
   * @throws RuntimeException     any exception thrown by the provider gets rethrown,
   *                              checked exceptions will be wrapped into appropriate runtime exceptions.
   */
  public static synchronized boolean registerIfAbsent(final Callable<Tracer> provider) {
    requireNonNull(provider, "Cannot register GlobalTracer from provider <null>.");
    if (!isRegistered()) {
      try {
        final Tracer suppliedTracer = requireNonNull(provider.call(), "Cannot register GlobalTracer <null>.");
        if (!(suppliedTracer instanceof GlobalTracer)) {
          GlobalTracer.tracer = suppliedTracer;
          isRegistered = true;
          return true;
        }
      } catch (RuntimeException rte) {
        throw rte; // Re-throw as-is
      } catch (Exception ex) {
        throw new IllegalStateException("Exception obtaining tracer from provider: " + ex.getMessage(), ex);
      }
    }
    return false;
  }

  /**
   * Register a {@link Tracer} to back the behaviour of the {@link #get()}.
   * <p>
   * Registration is a one-time operation. Once a tracer has been registered, all attempts at re-registering
   * will return {@code false}. Use {@link #registerIfAbsent(Callable)} for lazy initiation to avoid multiple
   * instantiations of tracer.
   * <p>
   * Every application intending to use the global tracer is responsible for registering it once
   * during its initialization.
   *
   * @param tracer tracer to be registered.
   * @return {@code true} if the provided tracer was registered as a result of this call,
   * {@code false} otherwise.
   * @throws NullPointerException if the tracer {@code null}.
   * @throws RuntimeException     any exception thrown by the provider gets rethrown,
   *                              checked exceptions will be wrapped into appropriate runtime exceptions.
   * @see #registerIfAbsent(Callable)
   */
  public static synchronized boolean registerIfAbsent(final Tracer tracer) {
    requireNonNull(tracer, "Cannot register GlobalTracer. Tracer is null");
    return registerIfAbsent(new Callable<Tracer>() {
      @Override
      public Tracer call() {
        return tracer;
      }
    });
  }


  private static Callable<Tracer> provide(final Tracer tracer) {
    return new Callable<Tracer>() {
      public Tracer call() {
        return tracer;
      }
    };
  }

  private static <T> T requireNonNull(T value, String message) {
    if (value == null) {
      throw new NullPointerException(message);
    }
    return value;
  }

  @Override
  public ScopeManager scopeManager() {
    return tracer.scopeManager();
  }

  @Override
  public SpanBuilder buildSpan(String operationName) {
    return tracer.buildSpan(operationName);
  }

  @Override
  public <C> void inject(SpanContext spanContext, Format<C> format, C carrier) {
    tracer.inject(spanContext, format, carrier);
  }

  @Override
  public <C> SpanContext extract(Format<C> format, C carrier) {
    return tracer.extract(format, carrier);
  }

  @Override
  public Span activeSpan() {
    return tracer.activeSpan();
  }

  @Override
  public Scope activateSpan(Span span) {
    return tracer.activateSpan(span);
  }

  @Override
  public void close() {
    tracer.close();
  }

  @Override
  public String toString() {
    return GlobalTracer.class.getSimpleName() + '{' + tracer + '}';
  }
}