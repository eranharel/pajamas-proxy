package com.outbrain.pajamasproxy.util;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * A utility class for parsing strings of the form "host1:port1 host2:port2", into a list of {@link InetSocketAddress}.
 * IPv6 addresses are also supported.
 * 
 * @author Eran Harel
 */
public class SocketAddressUtil {

  private static final Logger log = LoggerFactory.getLogger(SocketAddressUtil.class);

  private static final Pattern IP_PATTERN = Pattern.compile("(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}):(\\d+)");
  private static final Pattern HOST_PATTERN = Pattern.compile("([\\w\\.\\-]+):(\\d+)");
  private static final AddressParser[] PARSERS = { new RegExAddressParser(HOST_PATTERN), new RegExAddressParser(IP_PATTERN), new IpV6AddressParser() };

  /**
   * Parses a string of the form "host1:port1 host2:port2", and returns a list of {@link InetSocketAddress}.
   * @param addressesStr a whitespace delimited host:port string to parse.
   * @return a parsed list of {@link InetSocketAddress}
   */
  public List<InetSocketAddress> parseAddresses(final String addressesStr) {
    Assert.isTrue(StringUtils.isNotBlank(addressesStr), "addressesStr must not be blank");

    final String[] addressArr = addressesStr.split("\\s");
    final List<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>(addressArr.length);

    for (final String address : addressArr) {
      if (StringUtils.isEmpty(address)) {
        continue;
      }

      boolean parseFailed = true;
      for (final AddressParser parser : PARSERS) {
        final InetSocketAddress parsedAddress = parser.parse(address);
        if (parsedAddress != null) {
          addresses.add(parsedAddress);
          parseFailed = false;
          break;
        }
      }

      if (parseFailed) {
        throw new IllegalArgumentException("can't parse host address " + address);
      }
    }

    log.info("parseAddresses = {}", addresses);
    return addresses;
  }

  private static interface AddressParser {
    public InetSocketAddress parse(String address);
  }

  private static class RegExAddressParser implements AddressParser {
    private final Pattern pattern;

    public RegExAddressParser(final Pattern pattern) {
      this.pattern = pattern;
    }

    @Override
    public InetSocketAddress parse(final String address) {
      InetSocketAddress socketAddress = null;
      final Matcher matcher = pattern.matcher(address);

      if (matcher.matches()) {
        final String host = matcher.group(1);
        final int port = Integer.parseInt(matcher.group(2));
        socketAddress = new InetSocketAddress(host, port);
        return socketAddress;
      }

      return socketAddress;
    }
  }

  private static class IpV6AddressParser implements AddressParser {
    @Override
    public InetSocketAddress parse(final String address) {
      final int lastColonIndex = address.lastIndexOf(':');
      if (lastColonIndex < 1) {
        return null;
      }

      final String host = address.substring(0, lastColonIndex);
      final int port = Integer.parseInt(address.substring(lastColonIndex + 1));

      try {
        final InetAddress hostAddr = InetAddress.getByName(host);
        return new InetSocketAddress(hostAddr, port);
      } catch (final UnknownHostException e) {
        return null;
      }
    }
  }
}
