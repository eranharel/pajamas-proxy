package com.outbrain.pajamasproxy.memcached.adapter;

import net.spy.memcached.CachedData;

import org.junit.Assert;
import org.junit.Test;

import io.netty.buffer.Unpooled;

/**
 * Test cases for the {@link SpyCacheElementTranscoder}
 * 
 * @author Eran Harel
 */
public class SpyCacheElementTranscoderTest {

  private static final int FLAGS = 0x7;
  private static final String DATA = "test-data";
  private static final CachedData CACHED_DATA = new CachedData(FLAGS, DATA.getBytes(), 666);

  @Test
  public void testEncode() {
    final LocalCacheElement cacheElement = new LocalCacheElement(null, FLAGS, 0, 0);
    cacheElement.setData(Unpooled.wrappedBuffer(DATA.getBytes()));
    final CachedData cachedData = new SpyCacheElementTranscoder().encode(cacheElement);

    // verify that we don't copy byte arrays...
    Assert.assertSame("payload", cacheElement.getData().array(), cachedData.getData());
    Assert.assertEquals("flags", cacheElement.getFlags(), cachedData.getFlags());
  }

  @Test
  public void testDecode() {
    final CacheElement cacheElement = new SpyCacheElementTranscoder().decode(CACHED_DATA);
    Assert.assertSame("payload", CACHED_DATA.getData(), cacheElement.getData().array());
    Assert.assertEquals("flags", CACHED_DATA.getFlags(), cacheElement.getFlags());
  }

  @Test
  public void testAsyncDecode_null() {
    Assert.assertFalse("return value", new SpyCacheElementTranscoder().asyncDecode(null));
  }

  @Test
  public void testAsyncDecode_any() {
    Assert.assertFalse("return value", new SpyCacheElementTranscoder().asyncDecode(CACHED_DATA));
  }

  @Test
  public void testGetMaxSize() {
    Assert.assertEquals("max size", CachedData.MAX_SIZE, new SpyCacheElementTranscoder().getMaxSize());
  }

}
