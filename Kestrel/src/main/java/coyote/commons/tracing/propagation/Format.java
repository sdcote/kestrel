package coyote.commons.tracing.propagation;


/**
 * Format instances control the behavior of Tracer.inject and Tracer.extract (and also constrain the type of the
 * carrier parameter to same).
 * <p>
 * Most OpenTracing users will only reference the Format.Builtin constants. For example:
 *
 * <pre><code>
 * Tracer tracer = ...
 * coyote.commons.tracing.propagation.HttpHeaders httpCarrier = new AnHttpHeaderCarrier(httpRequest);
 * SpanContext spanCtx = tracer.extract(Format.Builtin.HTTP_HEADERS, httpCarrier);
 * </code></pre>
 */
public interface Format<C> {
  final class Builtin<C> implements Format<C> {
    /**
     * The TEXT_MAP format allows for arbitrary String-&gt;String map encoding of SpanContext state for
     * Tracer.inject and Tracer.extract.
     * <p>
     * Unlike HTTP_HEADERS, the builtin TEXT_MAP format expresses no constraints on keys or values.
     */
    public final static Format<TextMap> TEXT_MAP = new Builtin<TextMap>("TEXT_MAP");
    /**
     * Like {@link Builtin#TEXT_MAP} but specific for calling {@link coyote.commons.tracing.Tracer#inject} with.
     */
    public final static Format<TextMapInject> TEXT_MAP_INJECT = new Builtin<TextMapInject>("TEXT_MAP_INJECT");
    /**
     * Like {@link Builtin#TEXT_MAP} but specific for calling {@link coyote.commons.tracing.Tracer#extract} with.
     */
    public final static Format<TextMapExtract> TEXT_MAP_EXTRACT = new Builtin<TextMapExtract>("TEXT_MAP_EXTRACT");
    /**
     * The HTTP_HEADERS format allows for HTTP-header-compatible String-&gt;String map encoding of SpanContext state
     * for Tracer.inject and Tracer.extract.
     * <p>
     * I.e., keys written to the TextMap MUST be suitable for HTTP header keys (which are poorly defined but
     * certainly restricted); and similarly for values (i.e., URL-escaped and "not too long").
     */
    public final static Format<TextMap> HTTP_HEADERS = new Builtin<TextMap>("HTTP_HEADERS");
    /**
     * The BINARY format allows for unconstrained binary encoding of SpanContext state for Tracer.inject and
     * Tracer.extract.
     */
    public final static Format<Binary> BINARY = new Builtin<Binary>("BINARY");
    /**
     * Like {@link Builtin#BINARY} but specific for calling {@link coyote.commons.tracing.Tracer#inject} with.
     */
    public final static Format<BinaryInject> BINARY_INJECT = new Builtin<BinaryInject>("BINARY_INJECT");
    /**
     * Like {@link Builtin#BINARY} but specific for calling {@link coyote.commons.tracing.Tracer#extract} with.
     */
    public final static Format<BinaryExtract> BINARY_EXTRACT = new Builtin<BinaryExtract>("BINARY_EXTRACT");
    private final String name;

    private Builtin(String name) {
      this.name = name;
    }

    /**
     * @return Short name for built-in formats as they tend to show up in exception messages.
     */
    @Override
    public String toString() {
      return Builtin.class.getSimpleName() + "." + name;
    }
  }
}
