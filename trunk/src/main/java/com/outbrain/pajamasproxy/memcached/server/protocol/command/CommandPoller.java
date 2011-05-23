package com.outbrain.pajamasproxy.memcached.server.protocol.command;

public interface CommandPoller extends Runnable {

  @Override
  public abstract void run();

}