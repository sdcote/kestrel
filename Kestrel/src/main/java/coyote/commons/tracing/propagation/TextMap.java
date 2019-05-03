package coyote.commons.tracing.propagation;


/**
 * TextMap is a built-in carrier for Tracer.inject() and Tracer.extract(). TextMap implementations allows Tracers to
 * read and write key:value String pairs from arbitrary underlying sources of data.
 */
public interface TextMap extends TextMapInject, TextMapExtract {
}
