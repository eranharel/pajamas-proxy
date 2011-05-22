package com.outbrain.pajamasproxy.memcached.proxy.future;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public abstract class AbstractSpyFutureWrapper<SPYV, V> implements Future<V> {

  protected final Logger log = LoggerFactory.getLogger(getClass());

  private final Future<SPYV> future;

  public AbstractSpyFutureWrapper(final Future<SPYV> future) {
    Assert.notNull(future, "wrapped future may not be null");
    this.future = future;
  }

  @Override
  public boolean cancel(final boolean mayInterruptIfRunning) {
    return future.cancel(mayInterruptIfRunning);
  }

  @Override
  public boolean isCancelled() {
    return future.isCancelled();
  }

  @Override
  public boolean isDone() {
    return future.isDone();
  }

  @Override
  public V get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
    return convertSpyResponse(future.get(timeout, unit));
  }

  @Override
  public V get() throws InterruptedException, ExecutionException {
    return convertSpyResponse(future.get());
  }

  protected abstract V convertSpyResponse(SPYV spyResponse);
}
