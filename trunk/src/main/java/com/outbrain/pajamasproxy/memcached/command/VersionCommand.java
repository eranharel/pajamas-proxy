package com.outbrain.pajamasproxy.memcached.command;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;

import com.thimbleware.jmemcached.protocol.CommandMessage;

public class VersionCommand extends SimpleCommand {

  public VersionCommand(final ChannelHandlerContext channelHandlerContext, final CommandMessage command, final Channel channel, final String version) {
    super(channelHandlerContext, command, channel);
    responseMessage.version = version;
  }

}
