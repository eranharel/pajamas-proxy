package com.thimbleware.jmemcached.protocol.command;

import java.util.Map;
import java.util.Set;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;

import com.thimbleware.jmemcached.protocol.value.CommandMessage;

public class StatsCommand extends SimpleCommand {

  public StatsCommand(final ChannelHandlerContext channelHandlerContext, final CommandMessage command, final Channel channel, final Map<String, Set<String>> stats) {
    super(channelHandlerContext, command, channel);
    responseMessage.withStatResponse(stats);
  }

}
