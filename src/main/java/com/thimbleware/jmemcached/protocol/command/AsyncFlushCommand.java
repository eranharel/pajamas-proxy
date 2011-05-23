package com.thimbleware.jmemcached.protocol.command;

import java.util.concurrent.Future;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;

import com.thimbleware.jmemcached.protocol.value.CommandMessage;

public class AsyncFlushCommand extends AbstractAsyncCommand<Boolean> {

  public AsyncFlushCommand(final ChannelHandlerContext channelHandlerContext, final CommandMessage command, final Channel channel, final Future<Boolean> futureResponse) {
    super(channelHandlerContext, command, channel, futureResponse);
  }

  @Override
  protected void appendValueToResponse(final Boolean spyResponse) {
    responseMessage.withFlushResponse(spyResponse);
  }

}
