package coyote.commons.tracing;

import coyote.commons.tracing.propagation.Format;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BuiltinFormatTest {

  @Test
  public void test_HTTP_HEADERS_toString() {
    assertEquals("Builtin.HTTP_HEADERS", Format.Builtin.HTTP_HEADERS.toString());
  }

  @Test
  public void test_TEXT_MAP_toString() {
    assertEquals("Builtin.TEXT_MAP", Format.Builtin.TEXT_MAP.toString());
    assertEquals("Builtin.TEXT_MAP_INJECT", Format.Builtin.TEXT_MAP_INJECT.toString());
    assertEquals("Builtin.TEXT_MAP_EXTRACT", Format.Builtin.TEXT_MAP_EXTRACT.toString());
  }

  @Test
  public void test_BINARY_toString() {
    assertEquals("Builtin.BINARY", Format.Builtin.BINARY.toString());
    assertEquals("Builtin.BINARY_INJECT", Format.Builtin.BINARY_INJECT.toString());
    assertEquals("Builtin.BINARY_EXTRACT", Format.Builtin.BINARY_EXTRACT.toString());
  }

}
