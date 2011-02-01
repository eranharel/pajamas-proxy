package com.outbrain.pajamasproxy.util;

import java.net.InetSocketAddress;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Test cases for {@link SocketAddressUtil}.
 * 
 * @author Eran Harel
 */
public class SocketAddressUtilTest {

  private static final String HOST = "localhost";

  @Test(expected = IllegalArgumentException.class)
  public void testParseAddresses_empty() {
    new SocketAddressUtil().parseAddresses("");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseAddresses_blank() {
    new SocketAddressUtil().parseAddresses("    ");
  }

  @Test
  public void testParseAddresses_singleAddress() {
    final List<InetSocketAddress> parsedAddresses = new SocketAddressUtil().parseAddresses("localhost:11666");
    Assert.assertNotNull(parsedAddresses);
    Assert.assertEquals("num parsed addresses", 1, parsedAddresses.size());
    Assert.assertEquals("parsed address", new InetSocketAddress(HOST, 11666), parsedAddresses.get(0));
  }

  @Test
  public void testParseAddresses_3Addresses() {
    final List<InetSocketAddress> parsedAddresses = new SocketAddressUtil().parseAddresses(" localhost:11666 localhost:11667 localhost:11668 ");
    Assert.assertNotNull(parsedAddresses);
    Assert.assertEquals("num parsed addresses", 3, parsedAddresses.size());
    Assert.assertEquals("parsed address", new InetSocketAddress(HOST, 11666), parsedAddresses.get(0));
    Assert.assertEquals("parsed address", new InetSocketAddress(HOST, 11667), parsedAddresses.get(1));
    Assert.assertEquals("parsed address", new InetSocketAddress(HOST, 11668), parsedAddresses.get(2));
  }

  @Test
  public void testParseAddresses_2_IPv4() {
    final List<InetSocketAddress> parsedAddresses = new SocketAddressUtil().parseAddresses(" 127.0.0.1:11666    0.0.0.0:11667 ");
    Assert.assertNotNull(parsedAddresses);
    Assert.assertEquals("num parsed addresses", 2, parsedAddresses.size());
    Assert.assertEquals("parsed address", new InetSocketAddress("127.0.0.1", 11666), parsedAddresses.get(0));
    Assert.assertEquals("parsed address", new InetSocketAddress("0.0.0.0", 11667), parsedAddresses.get(1));
  }

  @Test
  public void testParseAddresses_3_IPv6() {
    final List<InetSocketAddress> parsedAddresses = new SocketAddressUtil().parseAddresses(" ::1:22666    0:0:0:0:0:0:0:1:22667 2001:db8:1234:0000:0000:0000:0000:0000:22668 ");
    Assert.assertNotNull(parsedAddresses);
    Assert.assertEquals("num parsed addresses", 3, parsedAddresses.size());
    Assert.assertEquals("parsed address", new InetSocketAddress("::1", 22666), parsedAddresses.get(0));
    Assert.assertEquals("parsed address", new InetSocketAddress("0:0:0:0:0:0:0:1", 22667), parsedAddresses.get(1));
    Assert.assertEquals("parsed address", new InetSocketAddress("2001:db8:1234:0000:0000:0000:0000:0000", 22668), parsedAddresses.get(2));
  }
}
