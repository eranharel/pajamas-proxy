package com.outbrain.pajamasproxy.memcached.proxy.future;

import java.util.concurrent.Future;

import net.spy.memcached.CASResponse;

import com.outbrain.pajamasproxy.memcached.proxy.value.StoreResponse;

public class CasFuture extends AbstractSpyFutureWrapper<CASResponse, StoreResponse> {

  public CasFuture(final Future<CASResponse> future) {
    super(future);
  }

  @Override
  protected StoreResponse convertSpyResponse(final CASResponse casResponse) {
    switch (casResponse) {
    case EXISTS:
      return StoreResponse.EXISTS;
    case NOT_FOUND:
      return StoreResponse.NOT_FOUND;
    case OK:
      return StoreResponse.STORED;
    default:
      throw new IllegalArgumentException("unexpected cas response value: " + casResponse);
    }
  }

}
