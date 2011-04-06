package com.thimbleware.jmemcached;

import static java.lang.String.valueOf;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Abstract implementation of a cache handler for the memcache daemon; provides some convenience methods and
 * a general framework for implementation
 */
public abstract class AbstractCache implements Cache {

  protected final AtomicLong started = new AtomicLong();

  protected final AtomicInteger getCmds = new AtomicInteger();
  protected final AtomicInteger setCmds = new AtomicInteger();
  protected final AtomicInteger getHits = new AtomicInteger();
  protected final AtomicInteger getMisses = new AtomicInteger();
  protected final AtomicLong casCounter = new AtomicLong(1);

  public AbstractCache() {
    initStats();
  }

  /**
   * @return the current time in seconds (from epoch), used for expiries, etc.
   */
  public static int Now() {
    return (int) (System.currentTimeMillis() / 1000);
  }

  protected abstract Set<Key> keys();

  @Override
  public abstract long getCurrentItems();

  @Override
  public abstract long getLimitMaxBytes();

  @Override
  public abstract long getCurrentBytes();


  @Override
  public final int getGetCmds() {
    return getCmds.get();
  }

  @Override
  public final int getSetCmds() {
    return setCmds.get();
  }

  @Override
  public final int getGetHits() {
    return getHits.get();
  }

  @Override
  public final int getGetMisses() {
    return getMisses.get();
  }

  /**
   * Return runtime statistics
   *
   * @param arg additional arguments to the stats command
   * @return the full command response
   */
  @Override
  public final Map<String, Set<String>> stat(final String arg) {
    final Map<String, Set<String>> result = new HashMap<String, Set<String>>();

    // stats we know
    multiSet(result, "version", MemCacheDaemon.memcachedVersion);
    multiSet(result, "cmd_gets", valueOf(getGetCmds()));
    multiSet(result, "cmd_sets", valueOf(getSetCmds()));
    multiSet(result, "get_hits", valueOf(getGetHits()));
    multiSet(result, "get_misses", valueOf(getGetMisses()));
    multiSet(result, "time", valueOf(valueOf(Now())));
    multiSet(result, "uptime", valueOf(Now() - this.started.longValue()));
    multiSet(result, "cur_items", valueOf(this.getCurrentItems()));
    multiSet(result, "limit_maxbytes", valueOf(this.getLimitMaxBytes()));
    multiSet(result, "current_bytes", valueOf(this.getCurrentBytes()));
    multiSet(result, "free_bytes", valueOf(Runtime.getRuntime().freeMemory()));

    // Not really the same thing precisely, but meaningful nonetheless. potentially this should be renamed
    multiSet(result, "pid", valueOf(Thread.currentThread().getId()));

    // stuff we know nothing about; gets faked only because some clients expect this
    multiSet(result, "rusage_user", "0:0");
    multiSet(result, "rusage_system", "0:0");
    multiSet(result, "connection_structures", "0");

    // TODO we could collect these stats
    multiSet(result, "bytes_read", "0");
    multiSet(result, "bytes_written", "0");

    return result;
  }

  private void multiSet(final Map<String, Set<String>> map, final String key, final String val) {
    Set<String> cur = map.get(key);
    if (cur == null) {
      cur = new HashSet<String>();
    }
    cur.add(val);
    map.put(key, cur);
  }

  /**
   * Initialize all statistic counters
   */
  protected void initStats() {
    started.set(System.currentTimeMillis());
    //        getCmds.set(0);
    //        setCmds.set(0);
    //        getHits.set(0);
    //        getMisses.set(0);


  }

  @Override
  public abstract void asyncEventPing();
}
