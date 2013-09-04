package com.outbrain.pajamasproxy.memcached.proxy.future;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.util.Assert;

import com.outbrain.pajamasproxy.memcached.adapter.CacheElement;

public class GetFuture extends AbstractSpyFutureWrapper<Object, CacheElement> {

  private final String key;
  private final AtomicInteger getHits;
  private final AtomicInteger getMisses;

  public GetFuture(final Future<Object> future, final String key, final AtomicInteger getHits, final AtomicInteger getMisses) {
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
      getMisses.incrementAndGet();
    } else {
      getHits.incrementAndGet();
    }

    return cacheElement;
  }

}
