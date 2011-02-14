package com.outbrain.pajamasproxy.memcached.monitor;

import java.util.concurrent.atomic.AtomicInteger;

import com.thimbleware.jmemcached.Cache;
import com.thimbleware.jmemcached.CacheElement;

public class Statistics implements StatisticsMBean {

  private final Cache<CacheElement> cache;

  // TODO these should be somehow fetched from JMemcached MemcachedCommandHandler
  private final AtomicInteger currConnectionCount = new AtomicInteger();
  private final AtomicInteger totalConnectionCount = new AtomicInteger();

  public Statistics(final Cache<CacheElement> cache) {
    this.cache = cache;
  }

  @Override
  public int getCurrentConnectionCount() {
    return currConnectionCount.get();
  }

  @Override
  public int getTotalConnectionCount() {
    return totalConnectionCount.get();
  }

  @Override
  public int getGetCommands() {
    return cache.getGetCmds();
  }

  @Override
  public int getSetCommands() {
    return cache.getSetCmds();
  }

  @Override
  public int getGetHits() {
    return cache.getGetHits();
  }

  @Override
  public int getGetMisses() {
    return cache.getGetMisses();
  }

}
