package com.outbrain.pajamasproxy.memcached.proxy;

import net.rubyeye.xmemcached.MemcachedClient;

public interface CacheClientFactory {
  public MemcachedClient createCacheClient();
}
