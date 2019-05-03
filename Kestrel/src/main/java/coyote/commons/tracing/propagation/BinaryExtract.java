package coyote.commons.tracing.propagation;


import java.nio.ByteBuffer;

/**
 * {@link BinaryExtract} is an interface defining the required operations for a binary carrier for
 * {@link coyote.commons.tracing.Tracer#extract} only. {@link BinaryExtract} is defined as inbound (extraction).
 * <p>
 * When called with {@link coyote.commons.tracing.Tracer#extract}, {@link #extractionBuffer} will be called to retrieve the {@link ByteBuffer}
 * containing the data used for {@link coyote.commons.tracing.SpanContext} extraction.
 */
public interface BinaryExtract {

  /**
   * Gets the buffer containing the data used for {@link coyote.commons.tracing.SpanContext} extraction.
   * <p>
   * It is an error to call this method when Binary is used
   * for {@link coyote.commons.tracing.SpanContext} injection.
   *
   * @return The buffer used for {@link coyote.commons.tracing.SpanContext} extraction.
   */
  ByteBuffer extractionBuffer();
}
