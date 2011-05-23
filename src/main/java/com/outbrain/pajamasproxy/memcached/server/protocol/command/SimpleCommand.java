package com.outbrain.pajamasproxy.memcached.server.protocol.command;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;

import com.outbrain.pajamasproxy.memcached.server.protocol.value.CommandMessage;


public class SimpleCommand extends AbstractCommand {

  public SimpleCommand(final ChannelHandlerContext channelHandlerContext, final CommandMessage command, final Channel channel) {
    super(channelHandlerContext, command, channel);
  }

  @Override
  public void execute() throws InterruptedException {
    Channels.fireMessageReceived(channelHandlerContext, responseMessage, channel.getRemoteAddress());
  }
}
