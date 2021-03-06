package com.outbrain.pajamasproxy.memcached.proxy.future;

import com.codahale.metrics.Counter;
import com.outbrain.pajamasproxy.memcached.adapter.CacheElement;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class GetMultiFuture extends AbstractSpyFutureWrapper<Map<String, Object>, CacheElement[]> {

  private final List<String> keys;
  private final Counter getHits;
  private final Counter getMisses;

  public GetMultiFuture(final Future<Map<String, Object>> future, final List<String> keys, final Counter getHits, final Counter getMisses) {
    super(future);
    Assert.notNull(keys, "keys may not be null");
    Assert.notNull(getHits, "getHits may not be null");
    Assert.notNull(getMisses, "getMisses may not be null");
    this.keys = keys;
    this.getHits = getHits;
    this.getMisses = getMisses;
  }

  @Override
  protected CacheElement[] convertSpyResponse(final Map<String, Object> spyResponse) {
    log.debug("spy get found keys={} for requested keys={}", spyResponse.keySet(), keys);
    final CacheElement[] response = new CacheElement[keys.size()];
    int i = 0;
    for (final String key : keys) {
      final CacheElement cacheElement = (CacheElement) spyResponse.get(key);
      if (null == cacheElement) {
        getMisses.inc();
      } else {
        getHits.inc();
      }

      response[i++] = cacheElement;
    }

    return response;
  }

}
