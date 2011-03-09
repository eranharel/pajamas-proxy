package com.outbrain.pajamasproxy.memcached.monitor;

import java.net.SocketAddress;
import java.util.Collection;

import net.spy.memcached.MemcachedClientIF;

import org.springframework.util.Assert;

/**
 * An implementation of the {@link CacheClusterMBean} interface. Interacts with the client side of the proxy.
 * 
 * @author Eran Harel
 */
class CacheCluster implements CacheClusterMBean {

  private final MemcachedClientIF memcachedClient;

  public CacheCluster(final MemcachedClientIF memcachedClient) {
    Assert.notNull(memcachedClient, "memcachedClient may not be null");
    this.memcachedClient = memcachedClient;
  }

  @Override
  public Collection<SocketAddress> getAvailableServers() {
    return memcachedClient.getAvailableServers();
  }

  @Override
  public Collection<SocketAddress> getUnavailableServers() {
    return memcachedClient.getUnavailableServers();
  }
}
