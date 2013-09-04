package com.outbrain.pajamasproxy.memcached.server.protocol.command;

import com.outbrain.pajamasproxy.memcached.adapter.CacheElement;
import com.outbrain.pajamasproxy.memcached.server.protocol.value.CommandMessage;
import io.netty.channel.Channel;

import java.util.concurrent.Future;

public class AsyncGetMultiCommand extends AbstractAsyncCommand<CacheElement[]> {

  public AsyncGetMultiCommand(final CommandMessage command, final Channel channel, final Future<CacheElement[]> futureResponse) {
    super(command, channel, futureResponse);
  }

  @Override
  protected void appendValueToResponse(final CacheElement[] elements) {
    responseMessage.withElements(elements);
  }
}
