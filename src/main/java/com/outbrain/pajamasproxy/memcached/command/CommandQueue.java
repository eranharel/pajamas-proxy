package com.outbrain.pajamasproxy.memcached.command;


public interface CommandQueue {

  public abstract void enqueueFutureResponse(final Command command);

}