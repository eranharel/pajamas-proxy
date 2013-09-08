package com.outbrain.pajamasproxy.memcached.monitor;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.outbrain.pajamasproxy.memcached.proxy.MemcachedProxyStatistics;
import com.outbrain.pajamasproxy.memcached.server.protocol.ServerConnectionStatistics;
import com.outbrain.pajamasproxy.memcached.server.protocol.binary.DecodingStatistics;

/**
 * Test cases for the {@link StatisticsMBean} implementation.
 * 
 * @author Eran Harel
 */
public class StatisticsTest {

  @Mock
  private MemcachedProxyStatistics proxyMock;

  @Mock
  private ServerConnectionStatistics connectionStatisticsMock;

  @Mock
  private DecodingStatistics decodingStatisticsMock;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

  @After
  public void teardown() {
    this.proxyMock = null;
    this.connectionStatisticsMock = null;
    this.decodingStatisticsMock = null;
  }

  @Test
  public void testGetCurrentConnectionCount() {
    final long expectedCount = 666;

    when(connectionStatisticsMock.getCurrentConnectionCount()).thenReturn(expectedCount);
    final long actualCount = createStatisticsMBean().getCurrentConnectionCount();

    assertEquals("getCurrentConnectionCount", expectedCount, actualCount);
  }

  @Test
  public void testGetTotalConnectionCount() {
    final long expectedCount = 999;

    when(connectionStatisticsMock.getTotalConnectionCount()).thenReturn(expectedCount);
    final long actualCount = createStatisticsMBean().getTotalConnectionCount();

    assertEquals("getTotalConnectionCount", expectedCount, actualCount);
  }

  @Test
  public void testGetGetCommands_afterInit() {
    assertEquals("get command count after init", 0, createStatisticsMBean().getGetCommands());
  }

  @Test
  public void testGetGetCommands() {
    final long expectedCommandCount = 8;

    when(proxyMock.getGetCommands()).thenReturn(expectedCommandCount);
    final long actualCommandCount = createStatisticsMBean().getGetCommands();
    assertEquals("get command count", expectedCommandCount, actualCommandCount);
  }

  @Test
  public void testGetSetCommands_afterInit() {
    assertEquals("set command count after init", 0, createStatisticsMBean().getSetCommands());
  }

  @Test
  public void testGetSetCommands() {
    final long expectedCommandCount = 9;

    when(proxyMock.getSetCommands()).thenReturn(expectedCommandCount);
    final long actualCommandCount = createStatisticsMBean().getSetCommands();
    assertEquals("set command count", expectedCommandCount, actualCommandCount);
  }

  @Test
  public void testGetGetHits_afterInit() {
    assertEquals("get hits after init", 0, createStatisticsMBean().getGetHits());
  }

  @Test
  public void testGetGetHits() {
    final long expectedCommandCount = 10;

    when(proxyMock.getGetHits()).thenReturn(expectedCommandCount);
    final long actualCommandCount = createStatisticsMBean().getGetHits();
    assertEquals("get hits count", expectedCommandCount, actualCommandCount);
  }

  @Test
  public void testGetGetMisses_afterInit() {
    assertEquals("get misses after init", 0, createStatisticsMBean().getGetHits());
  }

  @Test
  public void testGetGetMisses() {
    final long expectedCommandCount = 77;

    when(proxyMock.getGetMisses()).thenReturn(expectedCommandCount);
    final long actualCommandCount = createStatisticsMBean().getGetMisses();
    assertEquals("get miss count", expectedCommandCount, actualCommandCount);
  }

  @Test
  public void testErrors_afterInit() throws Exception {
    assertEquals("errors after init", 0, createStatisticsMBean().getErrors());
  }

  @Test
  public void testGetErrors() {
    final long expectedCommandCount = 345;

    when(proxyMock.getErrors()).thenReturn(expectedCommandCount);
    final long actualCommandCount = createStatisticsMBean().getErrors();
    assertEquals("error count", expectedCommandCount, actualCommandCount);
  }

  @Test
  public void testTimeouts_afterInit() throws Exception {
    assertEquals("timeouts after init", 0, createStatisticsMBean().getTimeouts());
  }

  @Test
  public void testGetTimeouts() {
    final long expectedCommandCount = 9090;

    when(proxyMock.getTimeouts()).thenReturn(expectedCommandCount);
    final long actualCommandCount = createStatisticsMBean().getTimeouts();
    assertEquals("timeout count", expectedCommandCount, actualCommandCount);
  }

  @Test
  public void testDecodingErrors() {
    final long expectedCommandCount = 987;

    when(decodingStatisticsMock.getDecodingErrors()).thenReturn(expectedCommandCount);
    final long actualCommandCount = createStatisticsMBean().getDecodingErrors();
    assertEquals("decoding errror count", expectedCommandCount, actualCommandCount);
  }

  private StatisticsMBean createStatisticsMBean() {
    return new Statistics(proxyMock, connectionStatisticsMock, decodingStatisticsMock);
  }
}
