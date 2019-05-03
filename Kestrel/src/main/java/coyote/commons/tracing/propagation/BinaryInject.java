package coyote.commons.tracing.propagation;


import java.nio.ByteBuffer;

/**
 * {@link BinaryInject} is an interface defining the required operations for a binary carrier for
 * {@link coyote.commons.tracing.Tracer#inject} only. {@link BinaryInject} is defined as outbound (injection).
 * <p>
 * When called with {@link coyote.commons.tracing.Tracer#inject}, {@link #injectionBuffer} will be called
 * to retrieve the actual {@link ByteBuffer} used for the {@link coyote.commons.tracing.SpanContext} injection.
 */
public interface BinaryInject {
  /**
   * Gets the buffer used to store data as part of {@link coyote.commons.tracing.SpanContext} injection.
   * <p>
   * The lenght parameter hints the buffer length required for
   * {@link coyote.commons.tracing.SpanContext} injection. The user may use this to allocate a new
   * ByteBuffer or resize an existing one.
   * <p>
   * It is an error to call this method when Binary is used
   * for {@link coyote.commons.tracing.SpanContext} extraction.
   *
   * @param length The buffer length required for {@link coyote.commons.tracing.SpanContext} injection.
   *               It needs to be larger than zero.
   * @return The buffer used for {@link coyote.commons.tracing.SpanContext} injection.
   */
  ByteBuffer injectionBuffer(int length);
}
