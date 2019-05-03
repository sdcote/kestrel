package coyote.commons.tracing;

public interface Tag<T> {
  String getKey();

  void set(Span span, T value);
}