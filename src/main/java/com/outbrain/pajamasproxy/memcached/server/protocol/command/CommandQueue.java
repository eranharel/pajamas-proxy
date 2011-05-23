package com.outbrain.pajamasproxy.memcached.server.protocol.command;


public interface CommandQueue {

  public abstract void enqueueFutureResponse(final Command command);

}