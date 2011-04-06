package com.thimbleware.jmemcached;

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

  boolean isBlocked();

  void block(long blockedUntil);

  long getBlockedUntil();

  CacheElement append(CacheElement element);

  CacheElement prepend(CacheElement element);

  LocalCacheElement.IncrDecrResult add(int mod);
}
