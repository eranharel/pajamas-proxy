package com.outbrain.pajamasproxy.memcached.proxy;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thimbleware.jmemcached.Cache;
import com.thimbleware.jmemcached.CacheElement;
import com.thimbleware.jmemcached.Key;

class CacheProxy implements Cache<CacheElement> {

  private static Logger log = LoggerFactory.getLogger(CacheProxy.class);

  private final MemcachedClient memcachedClient;

  public CacheProxy(final MemcachedClient memcachedClient) throws IOException {
    this.memcachedClient = memcachedClient;
  }

  @Override
  public com.thimbleware.jmemcached.Cache.DeleteResponse delete(final Key key, final int time) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public com.thimbleware.jmemcached.Cache.StoreResponse add(final CacheElement e) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public com.thimbleware.jmemcached.Cache.StoreResponse replace(final CacheElement e) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public com.thimbleware.jmemcached.Cache.StoreResponse append(final CacheElement element) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public com.thimbleware.jmemcached.Cache.StoreResponse prepend(final CacheElement element) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public com.thimbleware.jmemcached.Cache.StoreResponse set(final CacheElement e) {
    try {
      log.trace("val={}", e.getData().array());
      memcachedClient.set(toStringKey(e.getKey()), e.getExpire(), e);
    } catch (final InterruptedException e1) {
      Thread.currentThread().interrupt();
    } catch (final Exception e1) {
      e1.printStackTrace();

      return StoreResponse.NOT_STORED;
    }
    return StoreResponse.STORED;
  }

  @Override
  public com.thimbleware.jmemcached.Cache.StoreResponse cas(final Long cas_key, final CacheElement e) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Integer get_add(final Key key, final int mod) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public CacheElement[] get(final Key... keys) {
    final Key key = keys[0];
    CacheElement value = null;
    try {
      value = memcachedClient.get(toStringKey(key));
    } catch (final TimeoutException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (final InterruptedException e) {
      Thread.currentThread().interrupt();
    } catch (final MemcachedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return new CacheElement[] { value };
  }

  @Override
  public boolean flush_all() {
    try {
      memcachedClient.flushAll();
    } catch (final InterruptedException e) {
      Thread.currentThread().interrupt();
      return false;
    } catch (final Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  @Override
  public boolean flush_all(final int expire) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void close() throws IOException {
    memcachedClient.shutdown();
  }

  @Override
  public long getCurrentItems() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public long getLimitMaxBytes() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public long getCurrentBytes() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int getGetCmds() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int getSetCmds() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int getGetHits() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int getGetMisses() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Map<String, Set<String>> stat(final String arg) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void asyncEventPing() {
    // TODO Auto-generated method stub

  }

  private String toStringKey(final Key key) {
    return key.bytes.toString(Charset.forName("utf-8"));
  }

}