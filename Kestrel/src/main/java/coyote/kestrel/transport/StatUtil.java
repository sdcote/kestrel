package coyote.kestrel.transport;

import coyote.commons.DateUtil;
import coyote.dataframe.DataFrame;
import coyote.dataframe.marshal.JSONMarshaler;
import coyote.i13n.*;
import coyote.loader.log.Log;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.Map;

/**
 * Collection of utility methods for presenting StatsBoard data.
 */
public class StatUtil {
  private static final String ARM = "ARM";
  private static final String TIMER = "Timer";
  private static final String GAUGE = "Gauge";
  private static final String STATUS = "Status";
  private static final String COUNTER = "Counter";
  private static final String VERSION = "Version";
  private static final String HOSTNAME = "DnsName";
  private static final String OS_ARCH = "OSArch";
  private static final String OS_NAME = "OSName";
  private static final String OS_VERSION = "OSVersion";
  private static final String RUNTIME_NAME = "RuntimeName";
  private static final String RUNTIME_VENDOR = "RuntimeVendor";
  private static final String RUNTIME_VERSION = "RuntimeVersion";
  private static final String STARTED = "Started";
  private static final String STATE = "State";
  private static final String USER_NAME = "Account";
  private static final String VM_AVAIL_MEM = "AvailableMemory";
  private static final String VM_CURR_HEAP = "CurrentHeap";
  private static final String VM_FREE_HEAP = "FreeHeap";
  private static final String VM_FREE_MEM = "FreeMemory";
  private static final String VM_HEAP_PCT = "HeapPercentage";
  private static final String VM_MAX_HEAP = "MaxHeapSize";
  private static final String FIXTURE_ID = "InstanceId";
  private static final String FIXTURE_NAME = "InstanceName";
  private static final String HOST_ADDRESS = "IpAddress";
  private static final String NAME = "Name";
  private static final String UPTIME = "Uptime";


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
   * @return the statistics as a JSON string.
   */
  public static String dump(StatBoard stats) {
    return JSONMarshaler.toFormattedString(createStatus(stats));
  }

  private static DataFrame createStatus(StatBoard statboard) {

    DataFrame retval = new DataFrame();
    retval.add(NAME, STATUS);

    retval.add(FIXTURE_ID, statboard.getId());
    retval.add(OS_NAME, System.getProperty("os.name"));
    retval.add(OS_ARCH, System.getProperty("os.arch"));
    retval.add(OS_VERSION, System.getProperty("os.version"));
    retval.add(RUNTIME_VERSION, System.getProperty("java.version"));
    retval.add(RUNTIME_VENDOR, System.getProperty("java.vendor"));
    retval.add(RUNTIME_NAME, "Java");
    retval.add(STARTED, DateUtil.ISO8601Format(statboard.getStartedTime()));
    retval.add(UPTIME, DateUtil.formatSignificantElapsedTime((System.currentTimeMillis() - statboard.getStartedTime()) / 1000));
    retval.add(USER_NAME, System.getProperty("user.name"));
    retval.add(VM_AVAIL_MEM, statboard.getAvailableMemory());
    retval.add(VM_CURR_HEAP, statboard.getCurrentHeapSize());
    retval.add(VM_FREE_HEAP, statboard.getFreeHeapSize());
    retval.add(VM_FREE_MEM, statboard.getFreeMemory());
    retval.add(VM_MAX_HEAP, statboard.getMaxHeapSize());
    retval.add(VM_HEAP_PCT, statboard.getHeapPercentage());
    String text = statboard.getHostname();
    retval.add(HOSTNAME, (text == null) ? "unknown" : text);
    InetAddress addr = statboard.getHostIpAddress();
    retval.add(HOST_ADDRESS, (addr == null) ? "unknown" : addr.getHostAddress());

    DataFrame childPacket = new DataFrame();

    // get the list of component versions registered with the statboard
    Map<String, String> versions = statboard.getVersions();
    for (String key : versions.keySet()) {
      childPacket.add(key, versions.get(key));
    }
    retval.add(VERSION, childPacket);

    // Get all counters
    childPacket.clear();
    for (Iterator<Counter> it = statboard.getCounterIterator(); it.hasNext(); ) {
      Counter counter = it.next();
      childPacket.add(counter.getName(), counter.getValue());
    }
    retval.add(COUNTER, childPacket);

    // Get all states
    childPacket.clear();
    for (Iterator<State> it = statboard.getStateIterator(); it.hasNext(); ) {
      State state = it.next();
      if (state.getValue() != null) {
        childPacket.add(state.getName(), state.getValue());
      } else {
        Log.info("State " + state.getName() + " is null");
      }
    }
    retval.add(STATE, childPacket);

    childPacket.clear();
    for (Iterator<Gauge> it = statboard.getGaugeIterator(); it.hasNext(); ) {
      DataFrame packet = it.next().toFrame();
      if (packet != null) {
        childPacket.add(packet);
      }
    }
    retval.add(GAUGE, childPacket);

    childPacket.clear();
    for (Iterator<TimingMaster> it = statboard.getTimerIterator(); it.hasNext(); ) {
      DataFrame cap = it.next().toFrame();
      if (cap != null) {
        childPacket.add(cap);
      }
    }
    retval.add(TIMER, childPacket);

    childPacket.clear();
    for (Iterator<ArmMaster> it = statboard.getArmIterator(); it.hasNext(); ) {
      DataFrame cap = it.next().toFrame();
      if (cap != null) {
        childPacket.add(cap);
      }
    }
    retval.add(ARM, childPacket);

    return retval;
  }

}
