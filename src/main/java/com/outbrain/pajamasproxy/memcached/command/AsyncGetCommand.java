package com.outbrain.pajamasproxy.memcached.command;

import java.util.concurrent.Future;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;

import com.thimbleware.jmemcached.CacheElement;
import com.thimbleware.jmemcached.protocol.CommandMessage;


public class AsyncGetCommand extends AbstractAsyncCommand<CacheElement[]> {

  public AsyncGetCommand(final ChannelHandlerContext channelHandlerContext, final CommandMessage command, final Channel channel, final Future<CacheElement[]> futureResponse) {
    super(channelHandlerContext, command, channel, futureResponse);
  }

  @Override
  protected void appendValueToResponse(final CacheElement[] elements) {
    responseMessage.withElements(elements);
  }
}
