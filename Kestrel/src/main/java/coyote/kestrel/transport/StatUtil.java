package coyote.kestrel.transport;

import coyote.dataframe.DataFrame;
import coyote.dataframe.marshal.JSONMarshaler;
import coyote.i13n.Counter;
import coyote.i13n.StatBoard;
import coyote.i13n.State;
import coyote.i13n.TimingMaster;

import java.util.Iterator;
import java.util.Map;

/**
 * Collection of utility methods for presenting StatsBoard data.
 */
public class StatUtil {

  /**
   * @return All counters as attributes of a data frame.
   */
  public static DataFrame getCounterDataFrame(StatBoard stats) {
    final DataFrame retval = new DataFrame();

    for (final Iterator it = stats.getCounterIterator(); it.hasNext(); ) {
      final Counter cntr = (Counter) ((Map.Entry) it.next()).getValue();
      try {
        retval.put(cntr.getName(), new Long(cntr.getValue()));
      } catch (final Exception ignore) {
      }
    }
    return retval;
  }

  /**
   * @return All states as attributes of a data frame.
   */
  public static DataFrame getStateDataFrame(StatBoard stats) {
    final DataFrame retval = new DataFrame();

    for (final Iterator it = stats.getStateIterator(); it.hasNext(); ) {
      final State stayt = (State) ((Map.Entry) it.next()).getValue();
      try {
        retval.put(stayt.getName(), stayt.getValue());
      } catch (final Exception ignore) {
      }
    }
    return retval;
  }

  /**
   * @return All timers as attributes of a data frame.
   */
  public static DataFrame getTimerDataFrame(StatBoard stats) {
    final DataFrame retval = new DataFrame();

    for (final Iterator it = stats.getTimerIterator(); it.hasNext(); ) {
      final TimingMaster timer = (TimingMaster) ((Map.Entry) it.next()).getValue();
      retval.put(timer.getName(), timer.toFrame());
    }
    return retval;
  }

  /**
   * Dump all the stats in JSON format.
   *
   * @param stats the stats to dump
   *
   * @return the statistics as a JSON string.
   */
  public static String dump(StatBoard stats) {
    DataFrame retval = new DataFrame();
    retval.put("Timers", getTimerDataFrame(stats));
    retval.put("Counters", getCounterDataFrame(stats));
    retval.put("States", getStateDataFrame(stats));
    return JSONMarshaler.toFormattedString(retval);
  }


}
