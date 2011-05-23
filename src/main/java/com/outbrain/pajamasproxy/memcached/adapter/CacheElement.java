package com.outbrain.pajamasproxy.memcached.adapter;

import java.io.Serializable;

import org.jboss.netty.buffer.ChannelBuffer;

/**
 */
public interface CacheElement extends Serializable {
  public final static int THIRTY_DAYS = 2592000;

  int size();

  @Override
  int hashCode();

  int getExpire();

  int getFlags();

  ChannelBuffer getData();

  void setData(ChannelBuffer data);

  Key getKey();

  long getCasUnique();

  void setCasUnique(long casUnique);
}
