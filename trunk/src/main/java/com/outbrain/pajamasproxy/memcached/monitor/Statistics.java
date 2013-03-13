package com.outbrain.pajamasproxy.memcached.monitor;

import org.springframework.util.Assert;

import com.outbrain.pajamasproxy.memcached.proxy.MemcachedProxyStatistics;
import com.outbrain.pajamasproxy.memcached.server.protocol.ServerConnectionStatistics;
import com.outbrain.pajamasproxy.memcached.server.protocol.binary.DecodingStatistics;

/**
 * The implementation of the {@link StatisticsMBean} interface.
 * 
 * @author Eran Harel
 */
class Statistics implements StatisticsMBean {

  private final MemcachedProxyStatistics proxyStatistics;
  private final ServerConnectionStatistics connectionStatistics;
  private final DecodingStatistics decodingStatistics;

  public Statistics(final MemcachedProxyStatistics proxyStatistics, final ServerConnectionStatistics connectionStatistics, final DecodingStatistics decodingStatistics) {
    Assert.notNull(proxyStatistics, "proxyStatistics may not be null");
    Assert.notNull(connectionStatistics, "connectionStatistics may not be null");
    Assert.notNull(decodingStatistics, "decodingStatistics may not be null");
    this.proxyStatistics = proxyStatistics;
    this.connectionStatistics = connectionStatistics;
    this.decodingStatistics = decodingStatistics;
  }

  @Override
  public int getCurrentConnectionCount() {
    return connectionStatistics.getCurrentConnectionCount();
  }

  @Override
  public int getTotalConnectionCount() {
    return connectionStatistics.getTotalConnectionCount();
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

  @Override
  public long getDecodingErrors() {
    return decodingStatistics.getDecodingErrors();
  }
}
