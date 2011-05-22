package com.outbrain.pajamasproxy.memcached.command;

public interface Command {

  public abstract void execute() throws InterruptedException;

}