package coyote.commons.tracing;

public class StringTag extends AbstractTag<String> {
  public StringTag(String key) {
    super(key);
  }

  @Override
  public void set(Span span, String tagValue) {
    span.setTag(super.key, tagValue);
  }

}