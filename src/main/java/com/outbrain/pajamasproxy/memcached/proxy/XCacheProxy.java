package com.outbrain.pajamasproxy.memcached.proxy;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.rubyeye.xmemcached.MemcachedClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thimbleware.jmemcached.AbstractCache;
import com.thimbleware.jmemcached.CacheElement;
import com.thimbleware.jmemcached.Key;

class XCacheProxy extends AbstractCache<CacheElement> {

  private static final Charset DEFAULT_CHARSET = Charset.forName("utf-8");

  private static Logger log = LoggerFactory.getLogger(XCacheProxy.class);

  private final CacheClientFactory cacheClientFactory;

  public XCacheProxy(final CacheClientFactory cacheClientFactory) throws IOException {
    this.cacheClientFactory = cacheClientFactory;
  }

  private MemcachedClient getCacheClient() {
    return cacheClientFactory.createCacheClient();
  }

  @SuppressWarnings("deprecation")
  @Override
  public DeleteResponse delete(final Key key, final int time) {
    boolean wasSuccess = false;
    try {
      wasSuccess = getCacheClient().delete(toStringKey(key), time);
    } catch (final Exception e) {
      handleClientException("delete", e);
    }

    return wasSuccess ? DeleteResponse.DELETED : DeleteResponse.NOT_FOUND;
  }

  @Override
  public StoreResponse add(final CacheElement element) {
    boolean wasSuccess = false;
    try {
      wasSuccess = getCacheClient().add(toStringKey(element.getKey()), element.getExpire(), element);
    } catch (final Exception ex) {
      handleClientException("add", ex);
    }

    return storeResponse(wasSuccess);
  }

  @Override
  public StoreResponse replace(final CacheElement element) {
    boolean wasSuccess = false;

    try {
      wasSuccess = getCacheClient().replace(toStringKey(element.getKey()), element.getExpire(), element);
    } catch (final Exception ex) {
      handleClientException("replace", ex);
    }

    return storeResponse(wasSuccess);
  }

  @Override
  public StoreResponse append(final CacheElement element) {
    boolean wasSuccess = false;

    try {
      wasSuccess = getCacheClient().append(toStringKey(element.getKey()), element);
    } catch (final Exception ex) {
      handleClientException("append", ex);
    }

    return storeResponse(wasSuccess);
  }

  @Override
  public StoreResponse prepend(final CacheElement element) {
    boolean wasSuccess = false;

    try {
      wasSuccess = getCacheClient().prepend(toStringKey(element.getKey()), element);
    } catch (final Exception ex) {
      handleClientException("prepend", ex);
    }

    return storeResponse(wasSuccess);
  }

  @Override
  public StoreResponse set(final CacheElement e) {
    boolean wasSuccess = false;
    setCmds.incrementAndGet();
    try {
      wasSuccess = getCacheClient().set(toStringKey(e.getKey()), e.getExpire(), e);
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
      return Long.valueOf(getCacheClient().incr(toStringKey(key), mod)).intValue();
    } catch (final Exception e) {
      handleClientException("incr", e);
    }

    return null;
  }

  @Override
  public CacheElement[] get(final Key... keys) {
    getCmds.incrementAndGet();
    final List<String> stringKeys = new ArrayList<String>(keys.length);
    for (final Key key : keys) {
      stringKeys.add(toStringKey(key));
    }

    try {
      final Map<String, CacheElement> values = getCacheClient().get(stringKeys);
      final int hits = values.size();
      final int misses = keys.length - hits;
      getMisses.addAndGet(misses);
      getHits.addAndGet(hits);
      return values.isEmpty() ? null : values.values().toArray(new CacheElement[values.size()]);
    } catch (final Exception e) {
      handleClientException("get", e);
    }

    return null;
  }

  @Override
  public boolean flush_all() {
    try {
      getCacheClient().flushAll();
    } catch (final Exception e) {
      handleClientException("flushAll", e);
    }

    return true;
  }

  @Override
  public boolean flush_all(final int expire) {
    try {
      getCacheClient().flushAll(expire, 5000);
    } catch (final Exception e) {
      handleClientException("flushAll", e);
    }

    return true;
  }

  @Override
  public void close() throws IOException {
    getCacheClient().shutdown();
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
  public void asyncEventPing() {
    // TODO Auto-generated method stub

  }

  @Override
  protected Set<Key> keys() {
    throw new UnsupportedOperationException("WTF???");
  }

  private String toStringKey(final Key key) {
    return key.bytes.toString(DEFAULT_CHARSET);
  }

  private void handleClientException(final String command, final Exception e) {
    final String message = new StringBuilder("Failed to execute ").append(command).append(" command").toString();
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
