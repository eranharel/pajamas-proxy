package com.outbrain.pajamasproxy.memcached.proxy.future;

import java.util.concurrent.Future;

import com.thimbleware.jmemcached.StoreResponse;

public class StoreFuture extends AbstractSpyFutureWrapper<Boolean, StoreResponse> {

  public StoreFuture(final Future<Boolean> future) {
    super(future);
  }

  @Override
  protected StoreResponse convertSpyResponse(final Boolean succeeded) {
    return succeeded ? StoreResponse.STORED : StoreResponse.NOT_STORED;
  }

}
