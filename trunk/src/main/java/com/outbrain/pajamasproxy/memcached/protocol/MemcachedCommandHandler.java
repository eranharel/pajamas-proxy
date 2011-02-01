package com.outbrain.pajamasproxy.memcached.protocol;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.outbrain.pajamasproxy.memcached.locator.ChannelLocator;

public class MemcachedCommandHandler extends SimpleChannelUpstreamHandler {

  private static final Logger log = LoggerFactory.getLogger(MemcachedCommandHandler.class);

  private final ConnectionManager connectionManager;

  public MemcachedCommandHandler(final ConnectionManager connectionManager) {
    Assert.notNull(connectionManager, "connectionManager may not be null");
    this.connectionManager = connectionManager;
  }

  @Override
  public void channelOpen(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
    final Channel inboundChannel = e.getChannel();
    log.info("Recieved a new connection from {} - connecting to cluster...", inboundChannel.getRemoteAddress());
    connectionManager.connectToCluster(inboundChannel);
  }

  @Override
  public void channelClosed(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
    final Channel inboundChannel = e.getChannel();
    connectionManager.clientDisconnected(inboundChannel);
  }

  @Override
  public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
    if (!(e.getMessage() instanceof MemcachedCommand)) {
      // can't handle - ignore.
      ctx.sendUpstream(e);
      return;
    }

    final MemcachedCommand command = (MemcachedCommand) e.getMessage();

    final ChannelLocator channelLocator = connectionManager.getChannelLocator(e.getChannel());
    if (command.getKey() == null) {
      //      for (final Channel outboundChannel : channelLocator.getAll()) {
      //        outboundChannel.write(command.getMessageBuffer());
      //      }
      final Channel outboundChannel = channelLocator.getChannel(new byte[4]);
      outboundChannel.write(command.getMessageBuffer());
    } else {
      final Channel outboundChannel = channelLocator.getChannel(command.getKey());
      outboundChannel.write(command.getMessageBuffer());
    }
  }


}
