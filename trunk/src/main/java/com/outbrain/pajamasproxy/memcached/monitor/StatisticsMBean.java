package com.outbrain.pajamasproxy.memcached.monitor;

/**
 * Aggregates the proxy statistics metrics.
 * 
 * @author Eran Harel
 */
public interface StatisticsMBean {

  public long getCurrentConnectionCount();

  public long getTotalConnectionCount();

  /**
   * @return The cache get commands count for the entire proxied cluster.
   */
  public long getGetCommands();

  /**
   * @return The cache set commands count for the entire proxied cluster.
   */
  public long getSetCommands();

  /**
   * @return The cache get hit count for the entire proxied cluster.
   */
  public long getGetHits();

  /**
   * @return The cache get miss count for the entire proxied cluster.
   */
  public long getGetMisses();

  /**
   * @return The number of timedout operations since startup.
   */
  public long getTimeouts();

  /**
   * @return The number of failed operations since startup.
   */
  public long getErrors();

  /**
   * @return the number of memcached commands decoding errors.
   */
  public long getDecodingErrors();
}
