package coyote.commons.tracing;


import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class TestUtils {

  public static Callable<Integer> finishedSpansSize(final MockTracer tracer) {
    return new Callable<Integer>() {
      @Override
      public Integer call() throws Exception {
        return tracer.finishedSpans().size();
      }
    };
  }

  public static List<MockSpan> getByTag(List<MockSpan> spans, AbstractTag key, Object value) {
    List<MockSpan> found = new ArrayList<>(spans.size());
    for (MockSpan span : spans) {
      if (span.tags().get(key.getKey()).equals(value)) {
        found.add(span);
      }
    }
    return found;
  }

  public static MockSpan getOneByTag(List<MockSpan> spans, AbstractTag key, Object value) {
    List<MockSpan> found = getByTag(spans, key, value);
    if (found.size() > 1) {
      throw new IllegalArgumentException("there is more than one span with tag '"
              + key.getKey() + "' and value '" + value + "'");
    }
    if (found.isEmpty()) {
      return null;
    } else {
      return found.get(0);
    }
  }

  public static void sleep() {
    try {
      TimeUnit.MILLISECONDS.sleep(new Random().nextInt(2000));
    } catch (InterruptedException e) {
      e.printStackTrace();
      Thread.currentThread().interrupt();
    }
  }

  public static void sleep(long milliseconds) {
    try {
      TimeUnit.MILLISECONDS.sleep(milliseconds);
    } catch (InterruptedException e) {
      e.printStackTrace();
      Thread.currentThread().interrupt();
    }
  }

  public static void sortByStartMicros(List<MockSpan> spans) {
    Collections.sort(spans, new Comparator<MockSpan>() {
      @Override
      public int compare(MockSpan o1, MockSpan o2) {
        return Long.compare(o1.startMicros(), o2.startMicros());
      }
    });
  }

  public static void assertSameTrace(List<MockSpan> spans) {
    for (int i = 0; i < spans.size() - 1; i++) {
      assertEquals(true, spans.get(spans.size() - 1).finishMicros() >= spans.get(i).finishMicros());
      assertEquals(spans.get(spans.size() - 1).context().traceId(), spans.get(i).context().traceId());
      assertEquals(spans.get(spans.size() - 1).context().spanId(), spans.get(i).parentId());
    }
  }
}