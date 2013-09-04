package com.outbrain.pajamasproxy.memcached.server.protocol.command;

import java.util.concurrent.Future;

import com.outbrain.pajamasproxy.memcached.server.protocol.value.CommandMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public class AsyncMutateCommand extends AbstractAsyncCommand<Long> {

  public AsyncMutateCommand(final CommandMessage command, final Channel channel,
      final Future<Long> futureResponse) {
    super(command, channel, futureResponse);
  }

  @Override
  protected void appendValueToResponse(final Long spyResponse) {
    responseMessage.withIncrDecrResponse(spyResponse.intValue());
  }

}
