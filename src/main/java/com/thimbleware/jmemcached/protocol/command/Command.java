package com.thimbleware.jmemcached.protocol.command;

public interface Command {

  public abstract void execute() throws InterruptedException;

}