package coyote.commons.tracing;


public final class NoopTracerFactory {
    
    public static NoopTracer create() {
        return NoopTracerImpl.INSTANCE;
    }

    private NoopTracerFactory() {}
}

