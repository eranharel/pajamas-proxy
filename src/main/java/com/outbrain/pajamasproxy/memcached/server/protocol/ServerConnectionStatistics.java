package com.outbrain.pajamasproxy.memcached.server.protocol;

/**
 * An API for enetities that provide connection count statistics
 * 
 * @author Eran Harel
 */
public interface ServerConnectionStatistics {

  public abstract long getCurrentConnectionCount();

  public abstract long getTotalConnectionCount();

}