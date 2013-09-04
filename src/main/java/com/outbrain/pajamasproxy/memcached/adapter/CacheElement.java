package com.outbrain.pajamasproxy.memcached.adapter;

import java.io.Serializable;

import io.netty.buffer.ByteBuf;

/**
 */
public interface CacheElement extends Serializable {
  public final static int THIRTY_DAYS = 2592000;

  int size();

  @Override
  int hashCode();

  int getExpire();

  int getFlags();

  ByteBuf getData();

  void setData(ByteBuf data);

  Key getKey();

  long getCasUnique();

  void setCasUnique(long casUnique);
}
