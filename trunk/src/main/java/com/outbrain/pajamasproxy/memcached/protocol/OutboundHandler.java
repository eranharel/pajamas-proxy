package com.outbrain.pajamasproxy.memcached.protocol;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OutboundHandler extends SimpleChannelUpstreamHandler {

  private static final Logger log = LoggerFactory.getLogger(OutboundHandler.class);
  private final Channel inboundChannel;

  OutboundHandler(final Channel inboundChannel) {
    this.inboundChannel = inboundChannel;
  }

  @Override
  public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
    inboundChannel.write(e.getMessage());
  }

  @Override
  public void channelClosed(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
    log.info("closing channel {}", e.getChannel());
    ChannelUtil.closeOnFlush(inboundChannel);
  }

  @Override
  public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e) throws Exception {
    log.error("Exception caught for channel " + e.getChannel(), e.getCause());
    ChannelUtil.closeOnFlush(e.getChannel());
  }
}
