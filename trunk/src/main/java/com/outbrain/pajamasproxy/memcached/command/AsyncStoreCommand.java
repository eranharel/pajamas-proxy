package com.outbrain.pajamasproxy.memcached.command;

import java.util.concurrent.Future;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;

import com.thimbleware.jmemcached.StoreResponse;
import com.thimbleware.jmemcached.protocol.CommandMessage;

public class AsyncStoreCommand extends AbstractAsyncCommand<StoreResponse> {

  public AsyncStoreCommand(final ChannelHandlerContext channelHandlerContext, final CommandMessage command, final Channel channel, final Future<StoreResponse> futureResponse) {
    super(channelHandlerContext, command, channel, futureResponse);
  }

  @Override
  protected void appendValueToResponse(final StoreResponse spyResponse) {
    responseMessage.withResponse(spyResponse);
  }

}
