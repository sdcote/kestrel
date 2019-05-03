package coyote.commons.tracing;


public class BooleanTag extends AbstractTag<Boolean> {
  public BooleanTag(String key) {
    super(key);
  }

  @Override
  public void set(Span span, Boolean tagValue) {
    span.setTag(super.key, tagValue);
  }
}

