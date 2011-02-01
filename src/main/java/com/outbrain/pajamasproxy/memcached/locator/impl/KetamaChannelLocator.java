package com.outbrain.pajamasproxy.memcached.locator.impl;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.outbrain.pajamasproxy.memcached.hash.HashAlgorithm;
import com.outbrain.pajamasproxy.memcached.locator.ChannelLocator;

public class KetamaChannelLocator implements ChannelLocator {

  private static final int NUM_REPITITIONS = 160;
  private final HashAlgorithm hashAlgorithm;
  private final NavigableMap<Long, Channel> hash2Channel = new TreeMap<Long, Channel>();

  private final ChannelGroup channelGroup = new DefaultChannelGroup();

  public KetamaChannelLocator(final HashAlgorithm hashAlgorithm, final Channel inboundChannel, final List<Channel> outboundChannels) {
    Assert.notNull(hashAlgorithm, "hashAlgorithm may not be null");
    Assert.notEmpty(outboundChannels, "channels may not be empty");

    this.hashAlgorithm = hashAlgorithm;

    channelGroup.add(inboundChannel);
    channelGroup.addAll(outboundChannels);

    // TODO taken from net.spy.memcached.KetamaNodeLocator - ask for permission
    for (final Channel channel : outboundChannels) {
      // Ketama does some special work with md5 where it reuses chunks.
      for (int i = 0; i < NUM_REPITITIONS / 4; i++) {
        final byte[] digest = md5(generateKeyForChannel(channel, i));
        for (int h = 0; h < 4; h++) {
          final Long key = ((long) (digest[3 + h * 4] & 0xFF) << 24) | ((long) (digest[2 + h * 4] & 0xFF) << 16) | ((long) (digest[1 + h * 4] & 0xFF) << 8) | (digest[h * 4] & 0xFF);
          hash2Channel.put(key, channel);
        }
      }
    }
  }

  private String generateKeyForChannel(final Channel channel, final int iteration) {
    return channel.getRemoteAddress() + "-" + iteration;
  }

  private byte[] md5(final String key) {
    MessageDigest md5;
    try {
      md5 = MessageDigest.getInstance("MD5");
    } catch (final NoSuchAlgorithmException e) {
      throw new RuntimeException("MD5 not supported", e);
    }
    md5.reset();
    md5.update(keyAsBytes(key));
    return md5.digest();
  }

  private byte[] keyAsBytes(final String key) {
    if (!StringUtils.hasLength(key)) {
      throw new IllegalArgumentException("Key must not be blank");
    }

    try {
      return key.getBytes("utf-8");
    } catch (final UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Channel getChannel(final byte[] key) {
    final Long hash = hashAlgorithm.hash(key);
    Channel channel = hash2Channel.get(hash);
    if (null == channel) {
      final Entry<Long, Channel> ceilingEntry = hash2Channel.ceilingEntry(hash);

      channel = (null == ceilingEntry ? hash2Channel.firstEntry().getValue() : ceilingEntry.getValue());
    }

    return channel;
  }

  @Override
  public void closeAll() {
    channelGroup.close();
  }
}
