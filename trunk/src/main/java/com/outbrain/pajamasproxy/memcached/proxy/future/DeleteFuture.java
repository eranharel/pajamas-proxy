package com.outbrain.pajamasproxy.memcached.proxy.future;

import java.util.concurrent.Future;

import com.outbrain.pajamasproxy.memcached.proxy.value.DeleteResponse;

public class DeleteFuture extends AbstractSpyFutureWrapper<Boolean, DeleteResponse> {

  public DeleteFuture(final Future<Boolean> future) {
    super(future);
  }

  @Override
  protected DeleteResponse convertSpyResponse(final Boolean succeeded) {
    return succeeded ? DeleteResponse.DELETED : DeleteResponse.NOT_FOUND;
  }

}
