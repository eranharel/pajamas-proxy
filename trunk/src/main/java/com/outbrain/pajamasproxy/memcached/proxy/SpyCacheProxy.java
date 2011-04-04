package com.outbrain.pajamasproxy.memcached.proxy;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import net.spy.memcached.CASResponse;
import net.spy.memcached.MemcachedClientIF;
import net.spy.memcached.OperationTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.thimbleware.jmemcached.AbstractCache;
import com.thimbleware.jmemcached.CacheElement;
import com.thimbleware.jmemcached.Key;

public class SpyCacheProxy extends AbstractCache<CacheElement> implements MemcachedProxyStatistics {

  private static final Charset DEFAULT_CHARSET = Charset.forName("utf-8");

  private static Logger log = LoggerFactory.getLogger(SpyCacheProxy.class);

  private final AtomicInteger errors = new AtomicInteger();
  private final AtomicInteger timeouts = new AtomicInteger();

  private final MemcachedClientIF memcachedClient;

  public SpyCacheProxy(final MemcachedClientIF memcachedClient) throws IOException {
    Assert.notNull(memcachedClient, "memcachedClient may not be null");
    this.memcachedClient = memcachedClient;
  }

  @Override
  public DeleteResponse delete(final Key key, final int time) {
    boolean wasSuccess = false;
    try {
      wasSuccess = memcachedClient.delete(toStringKey(key)).get();
    } catch (final Exception e) {
      handleClientException("delete", e);
    }

    return wasSuccess ? DeleteResponse.DELETED : DeleteResponse.NOT_FOUND;
  }

  @Override
  public StoreResponse add(final CacheElement element) {
    boolean wasSuccess = false;
    try {
      wasSuccess = memcachedClient.add(toStringKey(element.getKey()), element.getExpire(), element).get();
    } catch (final Exception e) {
      handleClientException("add", e);
    }

    return storeResponse(wasSuccess);
  }

  @Override
  public StoreResponse replace(final CacheElement element) {
    boolean wasSuccess = false;
    try {
      wasSuccess = memcachedClient.replace(toStringKey(element.getKey()), element.getExpire(), element).get();
    } catch (final Exception e) {
      handleClientException("replace", e);
    }

    return storeResponse(wasSuccess);
  }

  @Override
  public StoreResponse append(final CacheElement element) {
    boolean wasSuccess = false;
    try {
      wasSuccess = memcachedClient.append(element.getCasUnique(), toStringKey(element.getKey()), element).get();
    } catch (final Exception e) {
      handleClientException("append", e);
    }

    return storeResponse(wasSuccess);
  }

  @Override
  public StoreResponse prepend(final CacheElement element) {
    boolean wasSuccess = false;
    try {
      wasSuccess = memcachedClient.prepend(element.getCasUnique(), toStringKey(element.getKey()), element).get();
    } catch (final Exception e) {
      handleClientException("prepend", e);
    }

    return storeResponse(wasSuccess);
  }

  @Override
  public StoreResponse set(final CacheElement element) {
    boolean wasSuccess = false;
    setCmds.incrementAndGet();
    try {
      wasSuccess = memcachedClient.set(toStringKey(element.getKey()), element.getExpire(), element).get();
    } catch (final Exception e) {
      handleClientException("set", e);
    }

    return storeResponse(wasSuccess);
  }

  @Override
  public StoreResponse cas(final Long cas_key, final CacheElement element) {
    CASResponse casResponse = CASResponse.NOT_FOUND;
    try {
      casResponse = memcachedClient.cas(toStringKey(element.getKey()), cas_key, element);
    } catch (final Exception e) {
      handleClientException("cas", e);
    }

    return storeResponse(casResponse);
  }

  private StoreResponse storeResponse(final CASResponse casResponse) {
    switch (casResponse) {
    case EXISTS:
      return StoreResponse.EXISTS;
    case NOT_FOUND:
      return StoreResponse.NOT_FOUND;
    case OK:
      return StoreResponse.STORED;
    default:
      throw new RuntimeException("unexpected cas response value: " + casResponse);
    }
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
    getCmds.incrementAndGet();
    final List<String> stringKeys = new ArrayList<String>(keys.length);
    for (final Key key : keys) {
      stringKeys.add(toStringKey(key));
    }

    try {
      final Map<String, Object> values = memcachedClient.getBulk(stringKeys);

      updateGetStatistics(values, keys);

      final CacheElement[] response = new CacheElement[keys.length];
      int i = 0;
      for (final String key : stringKeys) {
        response[i++] = (CacheElement) values.get(key);
      }

      return response;
    } catch (final Exception e) {
      handleClientException("get", e);
    }

    return null;
  }

  private void updateGetStatistics(final Map<String, Object> values, final Key... keys) {
    final int hits = values.size();
    final int misses = keys.length - hits;
    getMisses.addAndGet(misses);
    getHits.addAndGet(hits);
  }

  @Override
  public boolean flush_all() {
    try {
      memcachedClient.flush();
    } catch (final Exception e) {
      handleClientException("flushAll", e);
    }

    return true;
  }

  @Override
  public boolean flush_all(final int expire) {
    try {
      memcachedClient.flush(expire);
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
  protected Set<Key> keys() {
    throw new UnsupportedOperationException("WTF???");
  }

  @Override
  public int getGetCommands() {
    return super.getGetCmds();
  }

  @Override
  public int getSetCommands() {
    return super.getSetCmds();
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

  private String toStringKey(final Key key) {
    return key.bytes.toString(DEFAULT_CHARSET);
  }

  @Override
  public int getErrors() {
    return errors.get();
  }

  @Override
  public int getTimeouts() {
    return timeouts.get();
  }

  private void handleClientException(final String command, final Exception e) {
    errors.incrementAndGet();

    final String message = new StringBuilder("Failed to execute ").append(command).append(" command").toString();
    log.error(message, e);
    if (e instanceof InterruptedException) {
      Thread.currentThread().interrupt();
    }
    if (e instanceof OperationTimeoutException) {
      timeouts.incrementAndGet();
    }

    throw new RuntimeException(message);
  }

  private com.thimbleware.jmemcached.Cache.StoreResponse storeResponse(final boolean succeeded) {
    return succeeded ? StoreResponse.STORED : StoreResponse.NOT_STORED;
  }
}
