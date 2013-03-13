package com.outbrain.pajamasproxy.memcached.monitor;

/**
 * Aggregates the proxy statistics metrics.
 * 
 * @author Eran Harel
 */
public interface StatisticsMBean {

  public int getCurrentConnectionCount();

  public int getTotalConnectionCount();

  /**
   * @return The cache get commands count for the entire proxied cluster.
   */
  public int getGetCommands();

  /**
   * @return The cache set commands count for the entire proxied cluster.
   */
  public int getSetCommands();

  /**
   * @return The cache get hit count for the entire proxied cluster.
   */
  public int getGetHits();

  /**
   * @return The cache get miss count for the entire proxied cluster.
   */
  public int getGetMisses();

  /**
   * @return The number of timedout operations since startup.
   */
  public abstract int getTimeouts();

  /**
   * @return The number of failed operations since startup.
   */
  public abstract int getErrors();

  /**
   * @return the number of memcached commands decoding errors.
   */
  public long getDecodingErrors();
}
