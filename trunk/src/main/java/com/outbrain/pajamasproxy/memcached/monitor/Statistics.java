package com.outbrain.pajamasproxy.memcached.monitor;

import java.util.concurrent.atomic.AtomicInteger;

import com.outbrain.pajamasproxy.memcached.proxy.MemcachedProxyStatistics;

/**
 * The implementation of the {@link StatisticsMBean} interface.
 * 
 * @author Eran Harel
 */
class Statistics implements StatisticsMBean {

  private final MemcachedProxyStatistics proxyStatistics;

  // TODO these should be somehow fetched from JMemcached MemcachedCommandHandler
  private final AtomicInteger currConnectionCount = new AtomicInteger();
  private final AtomicInteger totalConnectionCount = new AtomicInteger();

  public Statistics(final MemcachedProxyStatistics proxyStatistics) {
    this.proxyStatistics = proxyStatistics;
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
    return proxyStatistics.getGetCommands();
  }

  @Override
  public int getSetCommands() {
    return proxyStatistics.getSetCommands();
  }

  @Override
  public int getGetHits() {
    return proxyStatistics.getGetHits();
  }

  @Override
  public int getGetMisses() {
    return proxyStatistics.getGetMisses();
  }

  @Override
  public int getErrors() {
    return proxyStatistics.getErrors();
  }

  @Override
  public int getTimeouts() {
    return proxyStatistics.getTimeouts();
  }
}
