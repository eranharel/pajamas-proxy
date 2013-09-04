package com.outbrain.pajamasproxy.memcached.server.protocol.command;

import com.outbrain.pajamasproxy.memcached.server.protocol.value.CommandMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;


public class SimpleCommand extends AbstractCommand {

  public SimpleCommand(final CommandMessage command, final Channel channel) {
    super(command, channel);
  }

  @Override
  public void execute() throws InterruptedException {
    channel.writeAndFlush(responseMessage);
  }
}
