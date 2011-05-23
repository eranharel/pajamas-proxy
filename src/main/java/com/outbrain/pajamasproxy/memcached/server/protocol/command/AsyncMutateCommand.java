package com.outbrain.pajamasproxy.memcached.server.protocol.command;

import java.util.concurrent.Future;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;

import com.outbrain.pajamasproxy.memcached.server.protocol.value.CommandMessage;

public class AsyncMutateCommand extends AbstractAsyncCommand<Long> {

  public AsyncMutateCommand(final ChannelHandlerContext channelHandlerContext, final CommandMessage command, final Channel channel, final Future<Long> futureResponse) {
    super(channelHandlerContext, command, channel, futureResponse);
  }

  @Override
  protected void appendValueToResponse(final Long spyResponse) {
    responseMessage.withIncrDecrResponse(spyResponse.intValue());
  }

}
