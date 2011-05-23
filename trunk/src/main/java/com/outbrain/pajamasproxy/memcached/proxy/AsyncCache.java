package com.outbrain.pajamasproxy.memcached.proxy;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import com.outbrain.pajamasproxy.memcached.adapter.CacheElement;
import com.outbrain.pajamasproxy.memcached.adapter.Key;
import com.outbrain.pajamasproxy.memcached.proxy.value.DeleteResponse;
import com.outbrain.pajamasproxy.memcached.proxy.value.StoreResponse;

/**
 * An API for an asynchronous cache.
 */
public interface AsyncCache {

  /**
   * Handle the deletion of an item from the cache.
   *
   * @param key the key for the item
   * @return a future indicating the response status.
   */
  Future<DeleteResponse> delete(Key key);

  /**
   * Add an element to the cache
   *
   * @param e the element to add
   * @return a future indicating the response status.
   */
  Future<StoreResponse> add(CacheElement e);

  /**
   * Replace an element in the cache
   *
   * @param e the element to replace
   * @return a future indicating the response status.
   */
  Future<StoreResponse> replace(CacheElement e);

  /**
   * Append bytes to the end of an element in the cache
   *
   * @param element the element to append
   * @return a future indicating the response status.
   */
  Future<StoreResponse> append(CacheElement element);

  /**
   * Prepend bytes to the end of an element in the cache
   *
   * @param element the element to append
   * @return a future indicating the response status.
   */
  Future<StoreResponse> prepend(CacheElement element);

  /**
   * Set an element in the cache
   *
   * @param e the element to set
   * @return a future indicating the response status.
   */
  Future<StoreResponse> set(CacheElement e);

  /**
   * Set an element in the cache but only if the element has not been touched
   * since the last 'gets'
   * @param cas_key the cas key returned by the last gets
   * @param e the element to set
   * @return a future indicating the response status.
   */
  Future<StoreResponse> cas(Long cas_key, CacheElement e);

  /**
   * Increment an (integer) element in the cache
   * @param key the key to increment
   * @param mod the amount to add to the value
   * @return a future containing the new value (-1 if the key doesn't exist)
   */
  Future<Long> increment(Key key, int mod);

  /**
   * Decrement an (integer) element in the cache
   * @param key the key to increment
   * @param mod the amount to decrement from the value
   * @return a future containing the new value (-1 if the key doesn't exist)
   */
  Future<Long> decrement(Key key, int mod);

  /**
   * Get element(s) from the cache
   * @param keys the key for the element to lookup
   * @return the elements found for the specified key. May contain <code>null</code>s in case of cache misses.
   */
  Future<CacheElement[]> get(Collection<Key> keys);

  /**
   * Flush all cache entries
   * @return a future indicating the response status.
   */
  Future<Boolean> flushAll();

  /**
   * Flush all cache entries with a timestamp after a given expiration time
   * @param expire the flush time in seconds
   * @return a future indicating the response status.
   */
  Future<Boolean> flushAll(int expire);

  /**
   * Close the cache, freeing all resources on which it depends.
   * @throws IOException
   */
  void close() throws IOException;

  /**
   * @return the number of get commands executed
   */
  int getGetCommands();

  /**
   * @return the number of set commands executed
   */
  int getSetCommands();

  /**
   * @return the number of get hits
   */
  int getGetHits();

  /**
   * @return the number of stats
   */
  int getGetMisses();

  /**
   * Retrieve stats about the cache. If an argument is specified, a specific category of stats is requested.
   * @param arg a specific extended stat sub-category
   * @return a map of stats
   */
  Map<String, Set<String>> stats(String arg);

}
