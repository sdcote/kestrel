package coyote.commons.tracing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

public class NoopScopeManagerTest {

  @Test
  public void activeValueToleratesUseTest() {
    try{
      final Span active = NoopScopeManager.INSTANCE.activeSpan();
      assertNotNull(active);
      active.setTag("Foo","Bar");
    } catch (final NullPointerException e) {
      fail("NoopScopeManagerImpl.activeSpan() should return a usable span");
    }
  }
}
