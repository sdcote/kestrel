package coyote.commons.tracing;


public class IntOrStringTag extends IntTag {
  public IntOrStringTag(String key) {
    super(key);
  }

  public void set(Span span, String tagValue) {
    span.setTag(super.key, tagValue);
  }
}
