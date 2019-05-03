package coyote.commons.tracing;


public class IntTag extends AbstractTag<Integer> {
  public IntTag(String key) {
    super(key);
  }

  @Override
  public void set(Span span, Integer tagValue) {
    span.setTag(super.key, tagValue);
  }
}

