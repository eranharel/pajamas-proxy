package com.outbrain.pajamasproxy.memcached.monitor;

public interface StatisticsMBean {
  public int getCurrentConnectionCount();

  public int getTotalConnectionCount();

  public int getGetCommands();

  public int getSetCommands();

  public int getGetHits();

  public int getGetMisses();
}
