package com.outbrain.pajamasproxy.memcached.protocol;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFutureListener;

public class ChannelUtil {

  public static void closeOnFlush(final Channel channel) {
    if (channel.isConnected()) {
      channel.write(ChannelBuffers.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }
  }
}
