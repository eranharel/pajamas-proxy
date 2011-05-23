package com.outbrain.pajamasproxy.memcached.command;

import java.util.concurrent.Future;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;

import com.thimbleware.jmemcached.DeleteResponse;
import com.thimbleware.jmemcached.protocol.value.CommandMessage;

public class AsyncDeleteCommand extends AbstractAsyncCommand<DeleteResponse> {

  public AsyncDeleteCommand(final ChannelHandlerContext channelHandlerContext, final CommandMessage command, final Channel channel, final Future<DeleteResponse> futureResponse) {
    super(channelHandlerContext, command, channel, futureResponse);
  }

  @Override
  protected void appendValueToResponse(final DeleteResponse spyResponse) {
    responseMessage.withDeleteResponse(spyResponse);
  }

}
