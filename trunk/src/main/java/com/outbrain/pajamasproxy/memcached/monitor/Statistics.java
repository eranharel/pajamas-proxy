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
  public long getCurrentConnectionCount() {
    return connectionStatistics.getCurrentConnectionCount();
  }

  @Override
  public long getTotalConnectionCount() {
    return connectionStatistics.getTotalConnectionCount();
  }

  @Override
  public long getGetCommands() {
    return proxyStatistics.getGetCommands();
  }

  @Override
  public long getSetCommands() {
    return proxyStatistics.getSetCommands();
  }

  @Override
  public long getGetHits() {
    return proxyStatistics.getGetHits();
  }

  @Override
  public long getGetMisses() {
    return proxyStatistics.getGetMisses();
  }

  @Override
  public long getErrors() {
    return proxyStatistics.getErrors();
  }

  @Override
  public long getTimeouts() {
    return proxyStatistics.getTimeouts();
  }

  @Override
  public long getDecodingErrors() {
    return decodingStatistics.getDecodingErrors();
  }
}
