package coyote.commons.tracing.propagation;


/**
 * Binary is an interface defining the required operations for a binary carrier for
 * Tracer.inject() and Tracer.extract(). Binary can be defined either as inbound (extraction)
 * or outbound (injection).
 *
 * When Binary is defined as inbound, extractionBuffer() will be called to retrieve the ByteBuffer
 * containing the data used for SpanContext extraction.
 *
 * When Binary is defined as outbound, setInjectBufferLength() will be called in order to hint
 * the required buffer length to inject the SpanContext, and injectionBuffer() will be called
 * afterwards to retrieve the actual ByteBuffer used for the SpanContext injection.
 */
public interface Binary extends BinaryInject, BinaryExtract {
}
