package com.outbrain.pajamasproxy.memcached.server.protocol.command;

import java.util.concurrent.Future;

import com.outbrain.pajamasproxy.memcached.adapter.CacheElement;
import com.outbrain.pajamasproxy.memcached.server.protocol.value.CommandMessage;
import io.netty.channel.Channel;

public class AsyncGetCommand extends AbstractAsyncCommand<CacheElement> {

  public AsyncGetCommand(final CommandMessage command, final Channel channel, final Future<CacheElement> futureResponse) {
    super(command, channel, futureResponse);
  }

  @Override
  protected void appendValueToResponse(final CacheElement element) {
    responseMessage.withElements(new CacheElement[] {element});
  }
}
