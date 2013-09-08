package com.outbrain.pajamasproxy.memcached.proxy;

/**
 * An API for the proxy statistical metrics.
 * 
 * @author Eran Harel
 */
public interface MemcachedProxyStatistics {

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
}
