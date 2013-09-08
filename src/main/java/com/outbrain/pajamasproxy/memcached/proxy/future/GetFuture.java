package com.outbrain.pajamasproxy.memcached.proxy.future;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import com.codahale.metrics.Counter;
import org.springframework.util.Assert;

import com.outbrain.pajamasproxy.memcached.adapter.CacheElement;

public class GetFuture extends AbstractSpyFutureWrapper<Object, CacheElement> {

  private final String key;
  private final Counter getHits;
  private final Counter getMisses;

  public GetFuture(final Future<Object> future, final String key, final Counter getHits, final Counter getMisses) {
    super(future);
    Assert.notNull(key, "key may not be null");
    Assert.notNull(getHits, "getHits may not be null");
    Assert.notNull(getMisses, "getMisses may not be null");
    this.key = key;
    this.getHits = getHits;
    this.getMisses = getMisses;
  }

  @Override
  protected CacheElement convertSpyResponse(final Object spyResponse) {
    final CacheElement cacheElement = (CacheElement) spyResponse;
    if (null == cacheElement) {
      getMisses.inc();
    } else {
      getHits.inc();
    }

    return cacheElement;
  }

}
