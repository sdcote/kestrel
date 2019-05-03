package coyote.commons.tracing.propagation;



import java.nio.ByteBuffer;

public final class BinaryAdapters {

    private BinaryAdapters() {}

    /**
     * Creates an inbound Binary instance used for extraction with the
     * specified ByteBuffer as input.
     *
     * @param buffer The ByteBuffer used as input.
     *
     * @return The new {@link Binary} carrier used for extraction.
     */
    public static BinaryExtract extractionCarrier(ByteBuffer buffer) {
        if (buffer == null) {
            throw new NullPointerException();
        }

        return new BinaryExtractAdapter(buffer);
    }

    /**
     * Creates an outbound {@link Binary} instance used for injection with the
     * specified ByteBuffer as output. ByteBuffer.limit() will be set to the value
     * of the requested length at {@link BinaryInject#injectionBuffer} time, and
     * AssertionError will be thrown if the requested length is larger than
     * the remaining length of ByteBuffer.
     *
     * @param buffer The ByteBuffer used as input.
     *
     * @return The new Binary carrier used for injection.
     */
    public static BinaryInject injectionCarrier(ByteBuffer buffer) {
        return new BinaryInjectAdapter(buffer);
    }

    static class BinaryExtractAdapter implements BinaryExtract {
        ByteBuffer buffer;

        public BinaryExtractAdapter(ByteBuffer buffer) {
            this.buffer = buffer;
        }

        @Override
        public ByteBuffer extractionBuffer() {
            return buffer;
        }
    }

    static class BinaryInjectAdapter implements BinaryInject {
        ByteBuffer buffer;

        public BinaryInjectAdapter(ByteBuffer buffer) {
            this.buffer = buffer;
        }

        @Override
        public ByteBuffer injectionBuffer(int length) {
            if (length < 1) {
                throw new IllegalArgumentException("length needs to be larger than 0");
            }
            if (length > buffer.remaining()) {
                throw new AssertionError("length is larger than the backing ByteBuffer remaining length");
            }

            buffer.limit(buffer.position() + length);
            return buffer;
        }
    }
}
