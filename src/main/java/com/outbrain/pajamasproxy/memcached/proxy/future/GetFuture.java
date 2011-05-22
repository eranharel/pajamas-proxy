package com.outbrain.pajamasproxy.memcached.proxy.future;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.springframework.util.Assert;

import com.thimbleware.jmemcached.CacheElement;

public class GetFuture extends AbstractSpyFutureWrapper<Map<String, Object>, CacheElement[]> {

  private final List<String> keys;

  public GetFuture(final Future<Map<String, Object>> future, final List<String> keys) {
    super(future);
    Assert.notNull(keys, "keys may not be null");
    this.keys = keys;
  }

  @Override
  protected CacheElement[] convertSpyResponse(final Map<String, Object> spyResponse) {
    log.debug("spy get found keys={} for requested keys={}", spyResponse.keySet(), keys);
    final CacheElement[] response = new CacheElement[keys.size()];
    int i = 0;
    for (final String key : keys) {
      response[i++] = (CacheElement) spyResponse.get(key);
    }

    return response;
  }

}
