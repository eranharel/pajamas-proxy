package com.outbrain.pajamasproxy.memcached.monitor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

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
    final int expectedCount = 666;

    when(connectionStatisticsMock.getCurrentConnectionCount()).thenReturn(expectedCount);
    final int actualCount = createStatisticsMBean().getCurrentConnectionCount();

    assertEquals("getCurrentConnectionCount", expectedCount, actualCount);
  }

  @Test
  public void testGetTotalConnectionCount() {
    final int expectedCount = 999;

    when(connectionStatisticsMock.getTotalConnectionCount()).thenReturn(expectedCount);
    final int actualCount = createStatisticsMBean().getTotalConnectionCount();

    assertEquals("getTotalConnectionCount", expectedCount, actualCount);
  }

  @Test
  public void testGetGetCommands_afterInit() {
    assertEquals("get command count after init", 0, createStatisticsMBean().getGetCommands());
  }

  @Test
  public void testGetGetCommands() {
    final int expectedCommandCount = 8;

    when(proxyMock.getGetCommands()).thenReturn(expectedCommandCount);
    final int actualCommandCount = createStatisticsMBean().getGetCommands();
    assertEquals("get command count", expectedCommandCount, actualCommandCount);
  }

  @Test
  public void testGetSetCommands_afterInit() {
    assertEquals("set command count after init", 0, createStatisticsMBean().getSetCommands());
  }

  @Test
  public void testGetSetCommands() {
    final int expectedCommandCount = 9;

    when(proxyMock.getSetCommands()).thenReturn(expectedCommandCount);
    final int actualCommandCount = createStatisticsMBean().getSetCommands();
    assertEquals("set command count", expectedCommandCount, actualCommandCount);
  }

  @Test
  public void testGetGetHits_afterInit() {
    assertEquals("get hits after init", 0, createStatisticsMBean().getGetHits());
  }

  @Test
  public void testGetGetHits() {
    final int expectedCommandCount = 10;

    when(proxyMock.getGetHits()).thenReturn(expectedCommandCount);
    final int actualCommandCount = createStatisticsMBean().getGetHits();
    assertEquals("get hits count", expectedCommandCount, actualCommandCount);
  }

  @Test
  public void testGetGetMisses_afterInit() {
    assertEquals("get misses after init", 0, createStatisticsMBean().getGetHits());
  }

  @Test
  public void testGetGetMisses() {
    final int expectedCommandCount = 77;

    when(proxyMock.getGetMisses()).thenReturn(expectedCommandCount);
    final int actualCommandCount = createStatisticsMBean().getGetMisses();
    assertEquals("get miss count", expectedCommandCount, actualCommandCount);
  }

  @Test
  public void testErrors_afterInit() throws Exception {
    assertEquals("errors after init", 0, createStatisticsMBean().getErrors());
  }

  @Test
  public void testGetErrors() {
    final int expectedCommandCount = 345;

    when(proxyMock.getErrors()).thenReturn(expectedCommandCount);
    final int actualCommandCount = createStatisticsMBean().getErrors();
    assertEquals("error count", expectedCommandCount, actualCommandCount);
  }

  @Test
  public void testTimeouts_afterInit() throws Exception {
    assertEquals("timeouts after init", 0, createStatisticsMBean().getTimeouts());
  }

  @Test
  public void testGetTimeouts() {
    final int expectedCommandCount = 9090;

    when(proxyMock.getTimeouts()).thenReturn(expectedCommandCount);
    final int actualCommandCount = createStatisticsMBean().getTimeouts();
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
