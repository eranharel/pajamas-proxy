package com.outbrain.pajamasproxy.memcached.monitor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.thimbleware.jmemcached.Cache;
import com.thimbleware.jmemcached.CacheElement;

/**
 * Test cases for the {@link StatisticsMBean} implementation.
 * 
 * @author Eran Harel
 */
public class StatisticsTest {

  @Mock
  private Cache<CacheElement> cacheMock;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

  @After
  public void teardown() {
    this.cacheMock = null;
  }

  // TODO implement connection stats tests when stats available...
  //  @Test
  //  public void testGetCurrentConnectionCount() {
  //    fail("Not yet implemented");
  //  }
  //
  //  @Test
  //  public void testGetTotalConnectionCount() {
  //    fail("Not yet implemented");
  //  }

  @Test
  public void testGetGetCommands_afterInit() {
    assertEquals("get command count after init", 0, createStatisticsMBean().getGetCommands());
  }

  @Test
  public void testGetGetCommands() {
    final int expectedCommandCount = 8;

    when(cacheMock.getGetCmds()).thenReturn(expectedCommandCount);
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

    when(cacheMock.getSetCmds()).thenReturn(expectedCommandCount);
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

    when(cacheMock.getGetHits()).thenReturn(expectedCommandCount);
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

    when(cacheMock.getGetMisses()).thenReturn(expectedCommandCount);
    final int actualCommandCount = createStatisticsMBean().getGetMisses();
    assertEquals("get miss count", expectedCommandCount, actualCommandCount);
  }

  private StatisticsMBean createStatisticsMBean() {
    return new Statistics(cacheMock);
  }
}
