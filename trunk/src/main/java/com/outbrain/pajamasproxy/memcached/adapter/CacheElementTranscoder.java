package com.outbrain.pajamasproxy.memcached.adapter;

import net.rubyeye.xmemcached.transcoders.CachedData;
import net.rubyeye.xmemcached.transcoders.Transcoder;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.thimbleware.jmemcached.CacheElement;
import com.thimbleware.jmemcached.LocalCacheElement;

public class CacheElementTranscoder implements Transcoder<CacheElement> {

  @Override
  public CachedData encode(final CacheElement element) {
    final CachedData cachedData = new CachedData(element.getFlags(), element.getData().array());
    cachedData.setCas(element.getCasUnique());

    return cachedData;
  }

  @Override
  public CacheElement decode(final CachedData cachedData) {
    final LocalCacheElement element = new LocalCacheElement(null, cachedData.getFlag(), 0, cachedData.getCas());
    final ChannelBuffer data = ChannelBuffers.wrappedBuffer(cachedData.getData());
    element.setData(data);

    return element;
  }

  @Override
  public void setPrimitiveAsString(final boolean primitiveAsString) {
    notSupported();
  }

  @Override
  public void setPackZeros(final boolean packZeros) {
    notSupported();
  }

  @Override
  public void setCompressionThreshold(final int to) {
    notSupported();
  }

  @Override
  public boolean isPrimitiveAsString() {
    return false;
  }

  @Override
  public boolean isPackZeros() {
    return false;
  }

  private void notSupported() {
    throw new UnsupportedOperationException("not implemented");
  }

}
