package com.outbrain.pajamasproxy.memcached.proxy;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.rubyeye.xmemcached.MemcachedClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thimbleware.jmemcached.Cache;
import com.thimbleware.jmemcached.CacheElement;
import com.thimbleware.jmemcached.Key;

class CacheProxy implements Cache<CacheElement> {

  private static final Charset DEFAULT_CHARSET = Charset.forName("utf-8");

  private static Logger log = LoggerFactory.getLogger(CacheProxy.class);

  private final MemcachedClient memcachedClient;

  public CacheProxy(final MemcachedClient memcachedClient) throws IOException {
    this.memcachedClient = memcachedClient;
  }

  @SuppressWarnings("deprecation")
  @Override
  public DeleteResponse delete(final Key key, final int time) {
    boolean wasSuccess = false;
    try {
      wasSuccess = memcachedClient.delete(toStringKey(key), time);
    } catch (final Exception e) {
      handleClientException("delete", e);
    }

    return wasSuccess ? DeleteResponse.DELETED : DeleteResponse.NOT_FOUND;
  }

  @Override
  public StoreResponse add(final CacheElement element) {
    boolean wasSuccess = false;
    try {
      wasSuccess = memcachedClient.add(toStringKey(element.getKey()), element.getExpire(), element);
    } catch (final Exception ex) {
      handleClientException("add", ex);
    }

    return storeResponse(wasSuccess);
  }

  @Override
  public StoreResponse replace(final CacheElement element) {
    boolean wasSuccess = false;

    try {
      wasSuccess = memcachedClient.replace(toStringKey(element.getKey()), element.getExpire(), element);
    } catch (final Exception ex) {
      handleClientException("replace", ex);
    }

    return storeResponse(wasSuccess);
  }

  @Override
  public StoreResponse append(final CacheElement element) {
    boolean wasSuccess = false;

    try {
      wasSuccess = memcachedClient.append(toStringKey(element.getKey()), element);
    } catch (final Exception ex) {
      handleClientException("append", ex);
    }

    return storeResponse(wasSuccess);
  }

  @Override
  public StoreResponse prepend(final CacheElement element) {
    boolean wasSuccess = false;

    try {
      wasSuccess = memcachedClient.prepend(toStringKey(element.getKey()), element);
    } catch (final Exception ex) {
      handleClientException("prepend", ex);
    }

    return storeResponse(wasSuccess);
  }

  @Override
  public com.thimbleware.jmemcached.Cache.StoreResponse set(final CacheElement e) {
    boolean wasSuccess = false;
    try {
      wasSuccess = memcachedClient.set(toStringKey(e.getKey()), e.getExpire(), e);
    } catch (final Exception ex) {
      handleClientException("set", ex);
    }

    return storeResponse(wasSuccess);
  }

  @Override
  public StoreResponse cas(final Long cas_key, final CacheElement e) {
    throw new UnsupportedOperationException("cas");
  }

  @Override
  public Integer get_add(final Key key, final int mod) {
    try {
      return Long.valueOf(memcachedClient.incr(toStringKey(key), mod)).intValue();
    } catch (final Exception e) {
      handleClientException("incr", e);
    }

    return null;
  }

  @Override
  public CacheElement[] get(final Key... keys) {
    final List<String> stringKeys = new ArrayList<String>(keys.length);
    for (final Key key : keys) {
      stringKeys.add(toStringKey(key));
    }

    try {
      final Map<String, CacheElement> values = memcachedClient.get(stringKeys);
      return values.isEmpty() ? null : values.values().toArray(new CacheElement[values.size()]);
    } catch (final Exception e) {
      handleClientException("get", e);
    }

    return null;
  }

  @Override
  public boolean flush_all() {
    try {
      memcachedClient.flushAll();
    } catch (final Exception e) {
      handleClientException("flushAll", e);
    }

    return true;
  }

  @Override
  public boolean flush_all(final int expire) {
    try {
      memcachedClient.flushAll(expire, 5000);
    } catch (final Exception e) {
      handleClientException("flushAll", e);
    }

    return true;
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
    return Collections.emptyMap(); // this is just to avoid NPE until I implement this properly
  }

  @Override
  public void asyncEventPing() {
    // TODO Auto-generated method stub

  }

  private String toStringKey(final Key key) {
    return key.bytes.toString(DEFAULT_CHARSET);
  }

  private void handleClientException(final String command, final Exception e) {
    final String message = new StringBuilder("Failed to execute ").append(command).append("command").toString();
    log.error(message, e);
    if (e instanceof InterruptedException) {
      Thread.currentThread().interrupt();
    }

    throw new RuntimeException(message);
  }

  private com.thimbleware.jmemcached.Cache.StoreResponse storeResponse(final boolean succeeded) {
    return succeeded ? StoreResponse.STORED : StoreResponse.NOT_STORED;
  }
}