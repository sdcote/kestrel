package coyote.loader.log;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 *
 */
public class DepthTest {


  @DisplayName("Simple test successful")
  @Test
  public void test() {
    int depth = Log.getStackDepth();
    StringAppender logger = new StringAppender();
    Log.addLogger("StringAppender", logger);
    Log.startLogging(Log.DEBUG);
    Log.debug("This is a test: " + depth); // this line should be reported
    String entry = logger.toString();
    logger.clear();
    assertNotNull(entry);
    //System.out.println(entry);
    assertTrue(entry.contains("DepthTest.test():23"));
    Log.removeLogger("StringAppender");
  }

}
