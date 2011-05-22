package com.outbrain.pajamasproxy.memcached.command;

public interface CommandPoller extends Runnable {

  @Override
  public abstract void run();

}