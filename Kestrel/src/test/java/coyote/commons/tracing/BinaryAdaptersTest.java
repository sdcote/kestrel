package coyote.commons.tracing;

import coyote.commons.tracing.propagation.BinaryAdapters;
import coyote.commons.tracing.propagation.BinaryExtract;
import coyote.commons.tracing.propagation.BinaryInject;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BinaryAdaptersTest {

  @Test
  public void testExtractBinary() {
    ByteBuffer buff = ByteBuffer.wrap(new byte[0]);
    BinaryExtract binary = BinaryAdapters.extractionCarrier(buff);
    assertEquals(buff, binary.extractionBuffer());
  }

//  @Test(expected = NullPointerException.class)
//  public void testExtractBinaryNull() {
//    BinaryAdapters.extractionCarrier(null);
//  }

  @Test
  public void testInjectBinary() {
    ByteBuffer buffer = ByteBuffer.allocate(1);
    BinaryInject binary = BinaryAdapters.injectionCarrier(buffer);
    assertEquals(buffer, binary.injectionBuffer(1));
    assertEquals(0, buffer.position());
  }

//  @Test(expected = IllegalArgumentException.class)
//  public void testInjectBinaryInvalidLength() {
//    BinaryInject binary = BinaryAdapters.injectionCarrier(ByteBuffer.allocate(1));
//    binary.injectionBuffer(0);
//  }

//  @Test(expected = AssertionError.class)
//  public void testInjectBinaryLargerLength() {
//    BinaryInject binary = BinaryAdapters.injectionCarrier(ByteBuffer.allocate(1));
//    binary.injectionBuffer(2);
//  }
}
