package coyote.commons.tracing;


/**
 * References is essentially a namespace for the official OpenTracing reference types.
 * <p>
 * References are used by Tracer.buildSpan() to describe the relationships between Spans.
 *
 * @see Tracer.SpanBuilder#addReference(String, SpanContext)
 */
public final class References {
  /**
   * See http://opentracing.io/spec/#causal-span-references for more information about CHILD_OF references
   */
  public static final String CHILD_OF = "child_of";
  /**
   * See http://opentracing.io/spec/#causal-span-references for more information about FOLLOWS_FROM references
   */
  public static final String FOLLOWS_FROM = "follows_from";

  private References() {
  }
}