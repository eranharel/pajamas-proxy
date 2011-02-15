package com.outbrain.pajamasproxy.memcached.adapter;

import net.rubyeye.xmemcached.transcoders.CachedData;

import org.jboss.netty.buffer.ChannelBuffers;
import org.junit.Assert;
import org.junit.Test;

import com.thimbleware.jmemcached.CacheElement;
import com.thimbleware.jmemcached.LocalCacheElement;

/**
 * Test cases for the {@link XCacheElementTranscoder}
 * 
 * @author Eran Harel
 */
public class XCacheElementTranscoderTest {
  private static final int FLAGS = 0x7;
  private static final String DATA = "test-data";
  private static final CachedData CACHED_DATA = new CachedData(FLAGS, DATA.getBytes(), 666, 0);

  @Test
  public void testEncode() {
    final LocalCacheElement cacheElement = new LocalCacheElement(null, FLAGS, 0, 0);
    cacheElement.setData(ChannelBuffers.wrappedBuffer(DATA.getBytes()));
    final CachedData cachedData = new XCacheElementTranscoder().encode(cacheElement);

    // verify that we don't copy byte arrays...
    Assert.assertSame("payload", cacheElement.getData().array(), cachedData.getData());
    Assert.assertEquals("flags", cacheElement.getFlags(), cachedData.getFlag());
  }

  @Test
  public void testDecode() {
    final CacheElement cacheElement = new XCacheElementTranscoder().decode(CACHED_DATA);
    Assert.assertSame("payload", CACHED_DATA.getData(), cacheElement.getData().array());
    Assert.assertEquals("flags", CACHED_DATA.getFlag(), cacheElement.getFlags());
  }

}
