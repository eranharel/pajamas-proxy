package com.outbrain.pajamasproxy.memcached.proxy;

import static java.lang.String.valueOf;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import net.spy.memcached.MemcachedClientIF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.outbrain.pajamasproxy.memcached.proxy.future.CasFuture;
import com.outbrain.pajamasproxy.memcached.proxy.future.DeleteFuture;
import com.outbrain.pajamasproxy.memcached.proxy.future.GetFuture;
import com.outbrain.pajamasproxy.memcached.proxy.future.StoreFuture;
import com.thimbleware.jmemcached.CacheElement;
import com.thimbleware.jmemcached.DeleteResponse;
import com.thimbleware.jmemcached.Key;
import com.thimbleware.jmemcached.MemCacheDaemon;
import com.thimbleware.jmemcached.StoreResponse;

class AsyncSpyCacheProxy implements AsyncCache, MemcachedProxyStatistics {

  private static final Logger log = LoggerFactory.getLogger(AsyncSpyCacheProxy.class);

  private final AtomicLong started = new AtomicLong();

  private final AtomicInteger getCmds = new AtomicInteger();
  private final AtomicInteger setCmds = new AtomicInteger();
  private final AtomicInteger getHits = new AtomicInteger();
  private final AtomicInteger getMisses = new AtomicInteger();
  //  private final AtomicLong casCounter = new AtomicLong(1);

  private static final Charset DEFAULT_CHARSET = Charset.forName("utf-8");

  // TODO these members are not being updated ATM...
  private final AtomicInteger errors = new AtomicInteger();
  private final AtomicInteger timeouts = new AtomicInteger();

  private final MemcachedClientIF memcachedClient;

  public AsyncSpyCacheProxy(final MemcachedClientIF memcachedClient) throws IOException {
    Assert.notNull(memcachedClient, "memcachedClient may not be null");
    this.memcachedClient = memcachedClient;
  }

  @Override
  public Future<DeleteResponse> delete(final Key key) {
    return new DeleteFuture(memcachedClient.delete(toStringKey(key)));
  }

  @Override
  public Future<StoreResponse> add(final CacheElement element) {
    return new StoreFuture(memcachedClient.add(toStringKey(element.getKey()), element.getExpire(), element));
  }

  @Override
  public Future<StoreResponse> replace(final CacheElement element) {
    return new StoreFuture(memcachedClient.replace(toStringKey(element.getKey()), element.getExpire(), element));
  }

  @Override
  public Future<StoreResponse> append(final CacheElement element) {
    return new StoreFuture(memcachedClient.append(element.getCasUnique(), toStringKey(element.getKey()), element));
  }

  @Override
  public Future<StoreResponse> prepend(final CacheElement element) {
    return new StoreFuture(memcachedClient.prepend(element.getCasUnique(), toStringKey(element.getKey()), element));
  }

  @Override
  public Future<StoreResponse> set(final CacheElement element) {
    setCmds.incrementAndGet();
    return new StoreFuture(memcachedClient.set(toStringKey(element.getKey()), element.getExpire(), element));
  }

  @Override
  public Future<StoreResponse> cas(final Long cas_key, final CacheElement element) {
    return new CasFuture(memcachedClient.asyncCAS(toStringKey(element.getKey()), cas_key, element));
  }

  @Override
  public Future<Long> increment(final Key key, final int mod) {
    return memcachedClient.asyncIncr(toStringKey(key), mod);
  }

  @Override
  public Future<Long> decrement(final Key key, final int mod) {
    return memcachedClient.asyncDecr(toStringKey(key), mod);
  }

  @Override
  public Future<CacheElement[]> get(final Collection<Key> keys) {
    getCmds.incrementAndGet();

    final List<String> stringKeys = extractStringKeys(keys);
    log.debug("get({})", stringKeys);

    return new GetFuture(memcachedClient.asyncGetBulk(stringKeys), stringKeys, getHits, getMisses);
  }

  private List<String> extractStringKeys(final Collection<Key> keys) {
    final List<String> stringKeys = new ArrayList<String>(keys.size());
    for (final Key key : keys) {
      stringKeys.add(toStringKey(key));
    }
    return stringKeys;
  }

  @Override
  public Future<Boolean> flushAll() {
    return memcachedClient.flush();
  }

  @Override
  public Future<Boolean> flushAll(final int expire) {
    return memcachedClient.flush(expire);
  }

  /**
   * Return runtime statistics
   *
   * @param arg additional arguments to the stats command
   * @return the full command response
   */
  @Override
  public Map<String, Set<String>> stats(final String arg) {
    final Map<String, Set<String>> result = new HashMap<String, Set<String>>();

    // stats we know
    multiSet(result, "version", MemCacheDaemon.memcachedVersion);
    multiSet(result, "cmd_gets", valueOf(getGetCommands()));
    multiSet(result, "cmd_sets", valueOf(getSetCommands()));
    multiSet(result, "get_hits", valueOf(getGetHits()));
    multiSet(result, "get_misses", valueOf(getGetMisses()));
    multiSet(result, "time", valueOf(valueOf(now())));
    multiSet(result, "uptime", valueOf(now() - this.started.longValue()));

    // TODO get these from spymemcached if we can
    //    multiSet(result, "cur_items", valueOf(this.getCurrentItems()));
    //    multiSet(result, "limit_maxbytes", valueOf(this.getLimitMaxBytes()));
    //    multiSet(result, "current_bytes", valueOf(this.getCurrentBytes()));
    multiSet(result, "free_bytes", valueOf(Runtime.getRuntime().freeMemory()));

    // Not really the same thing precisely, but meaningful nonetheless. potentially this should be renamed
    multiSet(result, "pid", valueOf(Thread.currentThread().getId()));

    // stuff we know nothing about; gets faked only because some clients expect this
    multiSet(result, "rusage_user", "0:0");
    multiSet(result, "rusage_system", "0:0");
    multiSet(result, "connection_structures", "0");

    // TODO we could collect these stats
    multiSet(result, "bytes_read", "0");
    multiSet(result, "bytes_written", "0");

    return result;
  }

  private void multiSet(final Map<String, Set<String>> map, final String key, final String val) {
    Set<String> cur = map.get(key);
    if (cur == null) {
      cur = new HashSet<String>();
    }
    cur.add(val);
    map.put(key, cur);
  }

  @Override
  public void close() throws IOException {
    memcachedClient.shutdown();
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

  @Override
  public int getGetCommands() {
    return getCmds.get();
  }

  @Override
  public final int getSetCommands() {
    return setCmds.get();
  }

  @Override
  public final int getGetHits() {
    return getHits.get();
  }

  @Override
  public final int getGetMisses() {
    return getMisses.get();
  }

  /**
   * @return the current time in seconds (from epoch).
   */
  private int now() {
    return (int) (System.currentTimeMillis() / 1000);
  }
}
