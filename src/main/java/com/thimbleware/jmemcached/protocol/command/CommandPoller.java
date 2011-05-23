package com.thimbleware.jmemcached.protocol.command;

public interface CommandPoller extends Runnable {

  @Override
  public abstract void run();

}