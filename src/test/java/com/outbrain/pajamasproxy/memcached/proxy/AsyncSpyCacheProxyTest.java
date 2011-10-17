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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import junit.framework.Assert;
import net.spy.memcached.CASResponse;
import net.spy.memcached.MemcachedClientIF;
import net.spy.memcached.internal.BulkFuture;
import net.spy.memcached.ops.OperationStatus;

import org.jboss.netty.buffer.ChannelBuffers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.outbrain.pajamasproxy.memcached.adapter.CacheElement;
import com.outbrain.pajamasproxy.memcached.adapter.Key;
import com.outbrain.pajamasproxy.memcached.adapter.LocalCacheElement;
import com.outbrain.pajamasproxy.memcached.proxy.value.DeleteResponse;
import com.outbrain.pajamasproxy.memcached.proxy.value.StoreResponse;

/**
 * Test cases for the {@link AsyncSpyCacheProxy} implementation.
 * 
 * @author Eran Harel
 */
public class AsyncSpyCacheProxyTest {

  private static final String TEST_KEY_STRING1 = "test-key1";
  private static final String TEST_KEY_STRING2 = "test-key2";
  private static Key KEY1 = new Key(ChannelBuffers.wrappedBuffer(TEST_KEY_STRING1.getBytes()));
  private static Key KEY2 = new Key(ChannelBuffers.wrappedBuffer(TEST_KEY_STRING2.getBytes()));

  private static final LocalCacheElement CACHE_ELEMENT = new LocalCacheElement(KEY1, 0x6, 10, 0);

  private static final Future<Boolean> SUCCESSFUL_OP = new MockFutureOperation<Boolean>(true);
  private static final Future<Boolean> FAILED_OP = new MockFutureOperation<Boolean>(false);

  @Mock
  private MemcachedClientIF clientMock;
  private AsyncSpyCacheProxy cacheProxy;

  @Before
  public void setup() throws IOException {
    MockitoAnnotations.initMocks(this);
    cacheProxy = new AsyncSpyCacheProxy(clientMock);
  }

  @After
  public void teardown() {
    clientMock = null;
    cacheProxy = null;
  }

  @Test
  public void testDelete_success() throws Exception {
    when(clientMock.delete(TEST_KEY_STRING1)).thenReturn(SUCCESSFUL_OP);

    final Future<DeleteResponse> deleteResponse = cacheProxy.delete(KEY1);

    verify(clientMock).delete(TEST_KEY_STRING1);
    assertEquals("delete response", DeleteResponse.DELETED, deleteResponse.get());
  }

  @Test
  public void testDelete_fail() throws Exception {
    when(clientMock.delete(TEST_KEY_STRING1)).thenReturn(FAILED_OP);

    final Future<DeleteResponse> deleteResponse = cacheProxy.delete(KEY1);

    verify(clientMock).delete(TEST_KEY_STRING1);
    assertEquals("delete response", DeleteResponse.NOT_FOUND, deleteResponse.get());
  }

  @Test
  public void testAdd_success() throws Exception {
    when(clientMock.add(TEST_KEY_STRING1, CACHE_ELEMENT.getExpire(), CACHE_ELEMENT)).thenReturn(SUCCESSFUL_OP);

    final Future<StoreResponse> storeResponse = cacheProxy.add(CACHE_ELEMENT);

    verify(clientMock).add(TEST_KEY_STRING1, CACHE_ELEMENT.getExpire(), CACHE_ELEMENT);
    assertEquals("add response", StoreResponse.STORED, storeResponse.get());
  }

  @Test
  public void testAdd_fail() throws Exception {
    when(clientMock.add(TEST_KEY_STRING1, CACHE_ELEMENT.getExpire(), CACHE_ELEMENT)).thenReturn(FAILED_OP);

    final Future<StoreResponse> storeResponse = cacheProxy.add(CACHE_ELEMENT);

    verify(clientMock).add(TEST_KEY_STRING1, CACHE_ELEMENT.getExpire(), CACHE_ELEMENT);
    assertEquals("add response", StoreResponse.NOT_STORED, storeResponse.get());
  }

  @Test
  public void testReplace_success() throws Exception {
    when(clientMock.replace(TEST_KEY_STRING1, CACHE_ELEMENT.getExpire(), CACHE_ELEMENT)).thenReturn(SUCCESSFUL_OP);

    final Future<StoreResponse> storeResponse = cacheProxy.replace(CACHE_ELEMENT);

    verify(clientMock).replace(TEST_KEY_STRING1, CACHE_ELEMENT.getExpire(), CACHE_ELEMENT);
    assertEquals("replace response", StoreResponse.STORED, storeResponse.get());
  }

  @Test
  public void testReplace_fail() throws Exception {
    when(clientMock.replace(TEST_KEY_STRING1, CACHE_ELEMENT.getExpire(), CACHE_ELEMENT)).thenReturn(FAILED_OP);

    final Future<StoreResponse> storeResponse = cacheProxy.replace(CACHE_ELEMENT);

    verify(clientMock).replace(TEST_KEY_STRING1, CACHE_ELEMENT.getExpire(), CACHE_ELEMENT);
    assertEquals("replace response", StoreResponse.NOT_STORED, storeResponse.get());
  }

  @Test
  public void testAppend_success() throws Exception {
    final long cas = 0;
    when(clientMock.append(cas, TEST_KEY_STRING1, CACHE_ELEMENT)).thenReturn(SUCCESSFUL_OP);

    final Future<StoreResponse> storeResponse = cacheProxy.append(CACHE_ELEMENT);

    verify(clientMock).append(cas, TEST_KEY_STRING1, CACHE_ELEMENT);
    assertEquals("append response", StoreResponse.STORED, storeResponse.get());
  }

  @Test
  public void testAppend_fail() throws Exception {
    final long cas = 0;
    when(clientMock.append(cas, TEST_KEY_STRING1, CACHE_ELEMENT)).thenReturn(FAILED_OP);

    final Future<StoreResponse> storeResponse = cacheProxy.append(CACHE_ELEMENT);

    verify(clientMock).append(cas, TEST_KEY_STRING1, CACHE_ELEMENT);
    assertEquals("append response", StoreResponse.NOT_STORED, storeResponse.get());
  }

  public void testPreppend_success() throws Exception {
    final long cas = 0;
    when(clientMock.prepend(cas, TEST_KEY_STRING1, CACHE_ELEMENT)).thenReturn(SUCCESSFUL_OP);

    final Future<StoreResponse> storeResponse = cacheProxy.prepend(CACHE_ELEMENT);

    verify(clientMock).prepend(cas, TEST_KEY_STRING1, CACHE_ELEMENT);
    assertEquals("prepend response", StoreResponse.STORED, storeResponse.get());
  }

  @Test
  public void testPreppend_fail() throws Exception {
    final long cas = 0;
    when(clientMock.prepend(cas, TEST_KEY_STRING1, CACHE_ELEMENT)).thenReturn(FAILED_OP);

    final Future<StoreResponse> storeResponse = cacheProxy.prepend(CACHE_ELEMENT);

    verify(clientMock).prepend(cas, TEST_KEY_STRING1, CACHE_ELEMENT);
    assertEquals("prepend response", StoreResponse.NOT_STORED, storeResponse.get());
  }

  @Test
  public void testSet_success() throws Exception {
    when(clientMock.set(TEST_KEY_STRING1, CACHE_ELEMENT.getExpire(), CACHE_ELEMENT)).thenReturn(SUCCESSFUL_OP);

    final Future<StoreResponse> storeResponse = cacheProxy.set(CACHE_ELEMENT);

    verify(clientMock).set(TEST_KEY_STRING1, CACHE_ELEMENT.getExpire(), CACHE_ELEMENT);
    assertEquals("set response", StoreResponse.STORED, storeResponse.get());
  }

  @Test
  public void testSet_fail() throws Exception {
    when(clientMock.set(TEST_KEY_STRING1, CACHE_ELEMENT.getExpire(), CACHE_ELEMENT)).thenReturn(FAILED_OP);

    final Future<StoreResponse> storeResponse = cacheProxy.set(CACHE_ELEMENT);

    verify(clientMock).set(TEST_KEY_STRING1, CACHE_ELEMENT.getExpire(), CACHE_ELEMENT);
    assertEquals("set response", StoreResponse.NOT_STORED, storeResponse.get());
  }

  @Test
  public void testCas_success() throws Exception {
    final long cas = 33L;
    when(clientMock.asyncCAS(TEST_KEY_STRING1, cas, CACHE_ELEMENT)).thenReturn(new MockFutureOperation<CASResponse>(CASResponse.OK));

    final Future<StoreResponse> storeResponse = cacheProxy.cas(cas, CACHE_ELEMENT);

    verify(clientMock).asyncCAS(TEST_KEY_STRING1, cas, CACHE_ELEMENT);
    assertEquals("cas response", StoreResponse.STORED, storeResponse.get());
  }

  @Test
  public void testCas_notFound() throws Exception {
    final long cas = 33L;
    when(clientMock.asyncCAS(TEST_KEY_STRING1, cas, CACHE_ELEMENT)).thenReturn(new MockFutureOperation<CASResponse>(CASResponse.NOT_FOUND));

    final Future<StoreResponse> storeResponse = cacheProxy.cas(cas, CACHE_ELEMENT);

    verify(clientMock).asyncCAS(TEST_KEY_STRING1, cas, CACHE_ELEMENT);
    assertEquals("cas response", StoreResponse.NOT_FOUND, storeResponse.get());
  }

  @Test
  public void testCas_exists() throws Exception {
    final long cas = 33L;
    when(clientMock.asyncCAS(TEST_KEY_STRING1, cas, CACHE_ELEMENT)).thenReturn(new MockFutureOperation<CASResponse>(CASResponse.EXISTS));

    final Future<StoreResponse> storeResponse = cacheProxy.cas(cas, CACHE_ELEMENT);

    verify(clientMock).asyncCAS(TEST_KEY_STRING1, cas, CACHE_ELEMENT);
    assertEquals("cas response", StoreResponse.EXISTS, storeResponse.get());
  }

  @Test
  public void testIncrement() throws Exception {
    final int inc = 38;
    final long response = 39L;
    when(clientMock.asyncIncr(TEST_KEY_STRING1, inc)).thenReturn(new MockFutureOperation<Long>(response));

    final Future<Long> incrementResponse = cacheProxy.increment(KEY1, inc);

    verify(clientMock).asyncIncr(TEST_KEY_STRING1, inc);
    assertEquals("get_add response", Long.valueOf(response), incrementResponse.get());
  }

  @Test
  public void testGet_allHits() throws Exception {
    final Collection<String> keys = Arrays.asList(TEST_KEY_STRING2, TEST_KEY_STRING1);
    final Map<String, Object> values = new HashMap<String, Object>();
    values.put(TEST_KEY_STRING1, CACHE_ELEMENT);
    final LocalCacheElement cacheElement2 = new LocalCacheElement(KEY2, 0x6, 10, 0);
    values.put(TEST_KEY_STRING2, cacheElement2);
    when(clientMock.asyncGetBulk(keys)).thenReturn(new MockBulkFuture<Map<String, Object>>(values));

    final Future<CacheElement[]> cacheElementsFuture = cacheProxy.get(Arrays.asList(KEY2, KEY1));
    final CacheElement[] cacheElements = cacheElementsFuture.get();

    assertEquals("num returned values", values.size(), cacheElements.length);
    assertEquals(cacheElement2, cacheElements[0]);
    assertEquals(CACHE_ELEMENT, cacheElements[1]);
  }

  @Test
  public void testGet_allMiss() throws Exception {
    final Collection<String> keys = Arrays.asList(TEST_KEY_STRING2, TEST_KEY_STRING1);
    final Map<String, Object> values = Collections.emptyMap();
    when(clientMock.asyncGetBulk(keys)).thenReturn(new MockBulkFuture<Map<String, Object>>(values));

    final Future<CacheElement[]> cacheElements = cacheProxy.get(Arrays.asList(KEY2, KEY1));

    assertNotNull("elements shouldn't be null", cacheElements);
    assertEquals("num returned values", 2, cacheElements.get().length);

    for (final CacheElement cacheElement : cacheElements.get()) {
      // TODO I'm not sure this is OK... it generates client warnings in WhalinTranscoder
      assertNull(cacheElement);
    }
  }

  @Test
  public void testFlush_all_success() throws Exception {
    when(clientMock.flush()).thenReturn(SUCCESSFUL_OP);
    Assert.assertTrue(cacheProxy.flushAll().get());
    verify(clientMock).flush();
  }

  @Test(expected = RuntimeException.class)
  public void testFlush_all_fail() throws Exception {
    when(clientMock.flush()).thenThrow(new TimeoutException());
    cacheProxy.flushAll();
  }

  @Test
  public void testFlush_all_delay_success() throws Exception {
    final int delay = 555;
    when(clientMock.flush(delay)).thenReturn(SUCCESSFUL_OP);
    Assert.assertTrue(cacheProxy.flushAll(delay).get());
    verify(clientMock).flush(delay);
  }

  @Test(expected = RuntimeException.class)
  public void testFlush_all_delay_fail() throws Exception {
    final int delay = 555;
    when(clientMock.flush(delay)).thenThrow(new TimeoutException());
    cacheProxy.flushAll(delay);
  }

  @Test
  public void testClose() throws IOException {
    cacheProxy.close();
    verify(clientMock).shutdown();
  }

  @Test
  public void testGetGetCmds() throws Exception {
    assertEquals("get commands before call", 0, cacheProxy.getGetCommands());

    final List<Key> keys = Arrays.asList(KEY1, KEY1);
    final Map<String, Object> values = Collections.emptyMap();
    when(clientMock.asyncGetBulk(Arrays.asList(TEST_KEY_STRING1, TEST_KEY_STRING1))).thenReturn(new MockBulkFuture<Map<String, Object>>(values));

    cacheProxy.get(keys);
    assertEquals("get commands after call", 1, cacheProxy.getGetCommands());
  }

  @Test
  public void testGetSetCmds() throws Exception {
    assertEquals("set commands before call", 0, cacheProxy.getSetCommands());
    when(clientMock.set(TEST_KEY_STRING1, CACHE_ELEMENT.getExpire(), CACHE_ELEMENT)).thenReturn(SUCCESSFUL_OP);
    cacheProxy.set(CACHE_ELEMENT);
    assertEquals("set commands after call", 1, cacheProxy.getSetCommands());
  }

  @Test
  public void testGetGetHitsAndMisses() throws Exception {
    assertEquals("get hits before call", 0, cacheProxy.getGetHits());
    assertEquals("get missess before call", 0, cacheProxy.getGetMisses());

    final Collection<String> keys = Arrays.asList(TEST_KEY_STRING2, TEST_KEY_STRING1);
    final Map<String, Object> values = new HashMap<String, Object>();
    values.put(TEST_KEY_STRING1, CACHE_ELEMENT);
    when(clientMock.asyncGetBulk(keys)).thenReturn(new MockBulkFuture<Map<String, Object>>(values));

    final Future<CacheElement[]> future = cacheProxy.get(Arrays.asList(KEY2, KEY1));
    future.get();

    assertEquals("get hits after call", 1, cacheProxy.getGetHits());
    assertEquals("get misses after call", 1, cacheProxy.getGetMisses());
  }

  private static class MockFutureOperation<V> implements Future<V> {

    private final V value;

    public MockFutureOperation(final V value) {
      this.value = value;
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
    public V get() throws InterruptedException, ExecutionException {
      return value;
    }

    @Override
    public V get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
      return value;
    }
  }

  private static class MockBulkFuture<V> extends MockFutureOperation<V> implements BulkFuture<V> {

    public MockBulkFuture(final V value) {
      super(value);
    }

    @Override
    public boolean isTimeout() {
      return false;
    }

    @Override
    public V getSome(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException {
      return null;
    }
    
    @Override
    public OperationStatus getStatus() {
    	// TODO Auto-generated method stub
    	return null;
    }
  }
}
