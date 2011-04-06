package com.thimbleware.jmemcached;

import java.io.Serializable;

import org.jboss.netty.buffer.ChannelBuffer;

import com.thimbleware.jmemcached.storage.hash.SizedItem;

/**
 */
public interface CacheElement extends Serializable, SizedItem {
  public final static int THIRTY_DAYS = 2592000;

  @Override
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
