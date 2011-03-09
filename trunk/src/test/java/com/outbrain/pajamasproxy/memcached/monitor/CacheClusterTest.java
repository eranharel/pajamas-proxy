package com.outbrain.pajamasproxy.memcached.monitor;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;

import junit.framework.Assert;
import net.spy.memcached.MemcachedClientIF;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Test cases for the {@link CacheCluster} MBean implementation.
 * 
 * @author Eran Harel
 */
public class CacheClusterTest {

  @Mock
  private MemcachedClientIF clientMock;

  private CacheCluster cacheCluster;

  private Collection<SocketAddress> expectedServers;

  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);
    cacheCluster = new CacheCluster(clientMock);

    expectedServers = new ArrayList<SocketAddress>();
    expectedServers.add(new SocketAdderssMock());
  }

  @After
  public void teardown() throws Exception {
    clientMock = null;
    cacheCluster = null;
    expectedServers = null;
  }

  @Test
  public void testGetAvailableServers() {
    Mockito.when(clientMock.getAvailableServers()).thenReturn(expectedServers);

    final Collection<SocketAddress> actualServers = cacheCluster.getAvailableServers();

    Assert.assertEquals(expectedServers, actualServers);
    Mockito.verify(clientMock).getAvailableServers();
  }

  @Test
  public void testGetUnavailableServers() {
    Mockito.when(clientMock.getUnavailableServers()).thenReturn(expectedServers);

    final Collection<SocketAddress> actualServers = cacheCluster.getUnavailableServers();

    Assert.assertEquals(expectedServers, actualServers);
    Mockito.verify(clientMock).getUnavailableServers();
  }

  private static class SocketAdderssMock extends SocketAddress {
    private static final long serialVersionUID = 1L;
  }
}
