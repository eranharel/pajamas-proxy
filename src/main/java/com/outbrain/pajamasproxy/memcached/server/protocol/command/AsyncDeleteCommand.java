package com.outbrain.pajamasproxy.memcached.server.protocol.command;

import java.util.concurrent.Future;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;

import com.outbrain.pajamasproxy.memcached.proxy.value.DeleteResponse;
import com.outbrain.pajamasproxy.memcached.server.protocol.value.CommandMessage;

public class AsyncDeleteCommand extends AbstractAsyncCommand<DeleteResponse> {

  public AsyncDeleteCommand(final ChannelHandlerContext channelHandlerContext, final CommandMessage command, final Channel channel, final Future<DeleteResponse> futureResponse) {
    super(channelHandlerContext, command, channel, futureResponse);
  }

  @Override
  protected void appendValueToResponse(final DeleteResponse spyResponse) {
    responseMessage.withDeleteResponse(spyResponse);
  }

}
