package com.thimbleware.jmemcached.protocol.command;


public interface CommandQueue {

  public abstract void enqueueFutureResponse(final Command command);

}