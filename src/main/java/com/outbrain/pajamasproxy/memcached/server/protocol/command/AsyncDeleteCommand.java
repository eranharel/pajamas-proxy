package com.outbrain.pajamasproxy.memcached.server.protocol.command;

import java.util.concurrent.Future;


import com.outbrain.pajamasproxy.memcached.proxy.value.DeleteResponse;
import com.outbrain.pajamasproxy.memcached.server.protocol.value.CommandMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public class AsyncDeleteCommand extends AbstractAsyncCommand<DeleteResponse> {

  public AsyncDeleteCommand(final CommandMessage command, final Channel channel, final Future<DeleteResponse> futureResponse) {
    super(command, channel, futureResponse);
  }

  @Override
  protected void appendValueToResponse(final DeleteResponse spyResponse) {
    responseMessage.withDeleteResponse(spyResponse);
  }

}
