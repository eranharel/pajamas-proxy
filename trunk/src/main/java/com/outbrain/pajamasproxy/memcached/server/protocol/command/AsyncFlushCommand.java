package com.outbrain.pajamasproxy.memcached.server.protocol.command;

import java.util.concurrent.Future;

import com.outbrain.pajamasproxy.memcached.server.protocol.value.CommandMessage;
import io.netty.channel.Channel;

public class AsyncFlushCommand extends AbstractAsyncCommand<Boolean> {

  public AsyncFlushCommand(final CommandMessage command, final Channel channel, final Future<Boolean> futureResponse) {
    super(command, channel, futureResponse);
  }

  @Override
  protected void appendValueToResponse(final Boolean spyResponse) {
    responseMessage.withFlushResponse(spyResponse);
  }

}
