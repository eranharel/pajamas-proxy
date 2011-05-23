package com.outbrain.pajamasproxy.memcached.server.protocol.command;

public interface Command {

  public abstract void execute() throws InterruptedException;

}