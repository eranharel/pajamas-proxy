package com.outbrain.pajamasproxy.memcached.adapter;

import net.spy.memcached.CachedData;
import net.spy.memcached.transcoders.Transcoder;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;


/**
 * An adapter between the Jmemcached server and Spymemcached client APIs.
 * 
 * @author Eran Harel
 */
public class SpyCacheElementTranscoder implements Transcoder<Object> {

  @Override
  public CachedData encode(final Object e) {
    final CacheElement element = (CacheElement) e;
    final CachedData cachedData = new CachedData(element.getFlags(), element.getData().array(), CachedData.MAX_SIZE);

    return cachedData;
  }

  @Override
  public CacheElement decode(final CachedData cachedData) {
    final LocalCacheElement element = new LocalCacheElement(null, cachedData.getFlags(), 0, /*cachedData.getCas()*/0);
    final ChannelBuffer data = ChannelBuffers.wrappedBuffer(cachedData.getData());
    element.setData(data);

    return element;
  }

  @Override
  public boolean asyncDecode(final CachedData d) {
    return false;
  }

  @Override
  public int getMaxSize() {
    return CachedData.MAX_SIZE;
  }

}
