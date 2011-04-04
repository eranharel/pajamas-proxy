package com.outbrain.pajamasproxy.memcached.proxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import junit.framework.Assert;
import net.spy.memcached.CASResponse;
import net.spy.memcached.MemcachedClientIF;

import org.jboss.netty.buffer.ChannelBuffers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.thimbleware.jmemcached.Cache.DeleteResponse;
import com.thimbleware.jmemcached.Cache.StoreResponse;
import com.thimbleware.jmemcached.CacheElement;
import com.thimbleware.jmemcached.Key;
import com.thimbleware.jmemcached.LocalCacheElement;

public class SpyCacheProxyTest {

  private static final String TEST_KEY_STRING1 = "test-key1";
  private static final String TEST_KEY_STRING2 = "test-key2";
  private static Key KEY1 = new Key(ChannelBuffers.wrappedBuffer(TEST_KEY_STRING1.getBytes()));
  private static Key KEY2 = new Key(ChannelBuffers.wrappedBuffer(TEST_KEY_STRING2.getBytes()));

  private static final LocalCacheElement CACHE_ELEMENT = new LocalCacheElement(KEY1, 0x6, 10, 0);

  private static final Future<Boolean> SUCCESSFUL_OP = new MockFutureOperation(true);
  private static final Future<Boolean> FAILED_OP = new MockFutureOperation(false);

  @Mock
  private MemcachedClientIF clientMock;
  private SpyCacheProxy cacheProxy;

  @Before
  public void setup() throws IOException {
    MockitoAnnotations.initMocks(this);
    cacheProxy = new SpyCacheProxy(clientMock);
  }

  @After
  public void teardown() {
    clientMock = null;
    cacheProxy = null;
  }

  @Test
  public void testDelete_success() {
    final int time = 30;

    when(clientMock.delete(TEST_KEY_STRING1)).thenReturn(SUCCESSFUL_OP);

    final DeleteResponse deleteResponse = cacheProxy.delete(KEY1, time);

    verify(clientMock).delete(TEST_KEY_STRING1);
    assertEquals("delete response", DeleteResponse.DELETED, deleteResponse);
  }

  @Test
  public void testDelete_fail() {
    final int time = 30;

    when(clientMock.delete(TEST_KEY_STRING1)).thenReturn(FAILED_OP);

    final DeleteResponse deleteResponse = cacheProxy.delete(KEY1, time);

    verify(clientMock).delete(TEST_KEY_STRING1);
    assertEquals("delete response", DeleteResponse.NOT_FOUND, deleteResponse);
  }

  @Test
  public void testAdd_success() {
    when(clientMock.add(TEST_KEY_STRING1, CACHE_ELEMENT.getExpire(), CACHE_ELEMENT)).thenReturn(SUCCESSFUL_OP);

    final StoreResponse storeResponse = cacheProxy.add(CACHE_ELEMENT);

    verify(clientMock).add(TEST_KEY_STRING1, CACHE_ELEMENT.getExpire(), CACHE_ELEMENT);
    assertEquals("add response", StoreResponse.STORED, storeResponse);
  }

  @Test
  public void testAdd_fail() {
    when(clientMock.add(TEST_KEY_STRING1, CACHE_ELEMENT.getExpire(), CACHE_ELEMENT)).thenReturn(FAILED_OP);

    final StoreResponse storeResponse = cacheProxy.add(CACHE_ELEMENT);

    verify(clientMock).add(TEST_KEY_STRING1, CACHE_ELEMENT.getExpire(), CACHE_ELEMENT);
    assertEquals("add response", StoreResponse.NOT_STORED, storeResponse);
  }

  @Test
  public void testReplace_success() {
    when(clientMock.replace(TEST_KEY_STRING1, CACHE_ELEMENT.getExpire(), CACHE_ELEMENT)).thenReturn(SUCCESSFUL_OP);

    final StoreResponse storeResponse = cacheProxy.replace(CACHE_ELEMENT);

    verify(clientMock).replace(TEST_KEY_STRING1, CACHE_ELEMENT.getExpire(), CACHE_ELEMENT);
    assertEquals("replace response", StoreResponse.STORED, storeResponse);
  }

  @Test
  public void testReplace_fail() {
    when(clientMock.replace(TEST_KEY_STRING1, CACHE_ELEMENT.getExpire(), CACHE_ELEMENT)).thenReturn(FAILED_OP);

    final StoreResponse storeResponse = cacheProxy.replace(CACHE_ELEMENT);

    verify(clientMock).replace(TEST_KEY_STRING1, CACHE_ELEMENT.getExpire(), CACHE_ELEMENT);
    assertEquals("replace response", StoreResponse.NOT_STORED, storeResponse);
  }

  @Test
  public void testAppend_success() {
    final long cas = 0;
    when(clientMock.append(cas, TEST_KEY_STRING1, CACHE_ELEMENT)).thenReturn(SUCCESSFUL_OP);

    final StoreResponse storeResponse = cacheProxy.append(CACHE_ELEMENT);

    verify(clientMock).append(cas, TEST_KEY_STRING1, CACHE_ELEMENT);
    assertEquals("append response", StoreResponse.STORED, storeResponse);
  }

  @Test
  public void testAppend_fail() {
    final long cas = 0;
    when(clientMock.append(cas, TEST_KEY_STRING1, CACHE_ELEMENT)).thenReturn(FAILED_OP);

    final StoreResponse storeResponse = cacheProxy.append(CACHE_ELEMENT);

    verify(clientMock).append(cas, TEST_KEY_STRING1, CACHE_ELEMENT);
    assertEquals("append response", StoreResponse.NOT_STORED, storeResponse);
  }

  public void testPreppend_success() {
    final long cas = 0;
    when(clientMock.prepend(cas, TEST_KEY_STRING1, CACHE_ELEMENT)).thenReturn(SUCCESSFUL_OP);

    final StoreResponse storeResponse = cacheProxy.prepend(CACHE_ELEMENT);

    verify(clientMock).prepend(cas, TEST_KEY_STRING1, CACHE_ELEMENT);
    assertEquals("prepend response", StoreResponse.STORED, storeResponse);
  }

  @Test
  public void testPreppend_fail() {
    final long cas = 0;
    when(clientMock.prepend(cas, TEST_KEY_STRING1, CACHE_ELEMENT)).thenReturn(FAILED_OP);

    final StoreResponse storeResponse = cacheProxy.prepend(CACHE_ELEMENT);

    verify(clientMock).prepend(cas, TEST_KEY_STRING1, CACHE_ELEMENT);
    assertEquals("prepend response", StoreResponse.NOT_STORED, storeResponse);
  }

  @Test
  public void testSet_success() {
    when(clientMock.set(TEST_KEY_STRING1, CACHE_ELEMENT.getExpire(), CACHE_ELEMENT)).thenReturn(SUCCESSFUL_OP);

    final StoreResponse storeResponse = cacheProxy.set(CACHE_ELEMENT);

    verify(clientMock).set(TEST_KEY_STRING1, CACHE_ELEMENT.getExpire(), CACHE_ELEMENT);
    assertEquals("set response", StoreResponse.STORED, storeResponse);
  }

  @Test
  public void testSet_fail() {
    when(clientMock.set(TEST_KEY_STRING1, CACHE_ELEMENT.getExpire(), CACHE_ELEMENT)).thenReturn(FAILED_OP);

    final StoreResponse storeResponse = cacheProxy.set(CACHE_ELEMENT);

    verify(clientMock).set(TEST_KEY_STRING1, CACHE_ELEMENT.getExpire(), CACHE_ELEMENT);
    assertEquals("set response", StoreResponse.NOT_STORED, storeResponse);
  }

  @Test
  public void testCas_success() {
    final long cas = 33L;
    when(clientMock.cas(TEST_KEY_STRING1, cas, CACHE_ELEMENT)).thenReturn(CASResponse.OK);

    final StoreResponse storeResponse = cacheProxy.cas(cas, CACHE_ELEMENT);

    verify(clientMock).cas(TEST_KEY_STRING1, cas, CACHE_ELEMENT);
    assertEquals("cas response", StoreResponse.STORED, storeResponse);
  }

  @Test
  public void testCas_notFound() {
    final long cas = 33L;
    when(clientMock.cas(TEST_KEY_STRING1, cas, CACHE_ELEMENT)).thenReturn(CASResponse.NOT_FOUND);

    final StoreResponse storeResponse = cacheProxy.cas(cas, CACHE_ELEMENT);

    verify(clientMock).cas(TEST_KEY_STRING1, cas, CACHE_ELEMENT);
    assertEquals("cas response", StoreResponse.NOT_FOUND, storeResponse);
  }

  @Test
  public void testCas_exists() {
    final long cas = 33L;
    when(clientMock.cas(TEST_KEY_STRING1, cas, CACHE_ELEMENT)).thenReturn(CASResponse.EXISTS);

    final StoreResponse storeResponse = cacheProxy.cas(cas, CACHE_ELEMENT);

    verify(clientMock).cas(TEST_KEY_STRING1, cas, CACHE_ELEMENT);
    assertEquals("cas response", StoreResponse.EXISTS, storeResponse);
  }

  @Test
  public void testGet_add() {
    final int inc = 38;
    final long response = 39L;
    when(clientMock.incr(TEST_KEY_STRING1, inc)).thenReturn(response);

    final Integer get_add_response = cacheProxy.get_add(KEY1, inc);

    verify(clientMock).incr(TEST_KEY_STRING1, inc);
    assertEquals("get_add response", Long.valueOf(response), Long.valueOf(get_add_response));
  }

  @Test
  public void testGet_allHits() {
    final Collection<String> keys = Arrays.asList(TEST_KEY_STRING2, TEST_KEY_STRING1);
    final Map<String, Object> values = new HashMap<String, Object>();
    values.put(TEST_KEY_STRING1, CACHE_ELEMENT);
    final LocalCacheElement cacheElement2 = new LocalCacheElement(KEY2, 0x6, 10, 0);
    values.put(TEST_KEY_STRING2, cacheElement2);
    when(clientMock.getBulk(keys)).thenReturn(values);

    final CacheElement[] cacheElements = cacheProxy.get(KEY2, KEY1);

    assertEquals("num returned values", values.size(), cacheElements.length);
    assertEquals(cacheElement2, cacheElements[0]);
    assertEquals(CACHE_ELEMENT, cacheElements[1]);
  }

  @Test
  public void testGet_allMiss() {
    final Collection<String> keys = Arrays.asList(TEST_KEY_STRING2, TEST_KEY_STRING1);
    final Map<String, Object> values = Collections.emptyMap();
    when(clientMock.getBulk(keys)).thenReturn(values);

    final CacheElement[] cacheElements = cacheProxy.get(KEY2, KEY1);

    assertNotNull("elements shouldn't be null", cacheElements);
    assertEquals("num returned values", 2, cacheElements.length);

    for (final CacheElement cacheElement : cacheElements) {
      // TODO I'm not sure this is OK... it generates client warnings in WhalinTranscoder
      assertNull(cacheElement);
    }
  }

  @Test
  public void testFlush_all_success() {
    Assert.assertTrue(cacheProxy.flush_all());
    verify(clientMock).flush();
  }

  @Test(expected = RuntimeException.class)
  public void testFlush_all_fail() {
    when(clientMock.flush()).thenThrow(new TimeoutException());
    cacheProxy.flush_all();
  }

  @Test
  public void testFlush_all_delay_success() {
    final int delay = 555;
    Assert.assertTrue(cacheProxy.flush_all(delay));
    verify(clientMock).flush(delay);
  }

  @Test(expected = RuntimeException.class)
  public void testFlush_all_delay_fail() {
    final int delay = 555;
    when(clientMock.flush(delay)).thenThrow(new TimeoutException());
    cacheProxy.flush_all(delay);
  }

  @Test
  public void testClose() throws IOException {
    cacheProxy.close();
    verify(clientMock).shutdown();
  }

  @Test
  public void testGetGetCmds() {
    assertEquals("get commands before call", 0, cacheProxy.getGetCmds());
    cacheProxy.get(KEY1, KEY1);
    assertEquals("get commands after call", 1, cacheProxy.getGetCmds());
  }

  @Test
  public void testGetSetCmds() {
    assertEquals("set commands before call", 0, cacheProxy.getSetCmds());
    when(clientMock.set(TEST_KEY_STRING1, CACHE_ELEMENT.getExpire(), CACHE_ELEMENT)).thenReturn(SUCCESSFUL_OP);
    cacheProxy.set(CACHE_ELEMENT);
    assertEquals("set commands after call", 1, cacheProxy.getSetCmds());
  }

  @Test
  public void testGetGetHitsAndMisses() {
    assertEquals("get hits before call", 0, cacheProxy.getGetHits());
    assertEquals("get missess before call", 0, cacheProxy.getGetMisses());

    final Collection<String> keys = Arrays.asList(TEST_KEY_STRING2, TEST_KEY_STRING1);
    final Map<String, Object> values = new HashMap<String, Object>();
    values.put(TEST_KEY_STRING1, CACHE_ELEMENT);
    when(clientMock.getBulk(keys)).thenReturn(values);

    cacheProxy.get(KEY2, KEY1);

    assertEquals("get hits after call", 1, cacheProxy.getGetHits());
    assertEquals("get misses after call", 1, cacheProxy.getGetMisses());
  }

  private static class MockFutureOperation implements Future<Boolean> {

    private final boolean success;

    public MockFutureOperation(final boolean success) {
      this.success = success;
    }

    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
      return false;
    }

    @Override
    public boolean isCancelled() {
      return false;
    }

    @Override
    public boolean isDone() {
      return true;
    }

    @Override
    public Boolean get() throws InterruptedException, ExecutionException {
      return success;
    }

    @Override
    public Boolean get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
      return success;
    }
  }
}
