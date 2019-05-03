package coyote.commons.tracing;


public abstract class AbstractTag<T> implements Tag<T> {
  protected final String key;

  public AbstractTag(String tagKey) {
    this.key = tagKey;
  }

  @Override
  public String getKey() {
    return key;
  }

  @Override
  public abstract void set(Span span, T tagValue);
}
