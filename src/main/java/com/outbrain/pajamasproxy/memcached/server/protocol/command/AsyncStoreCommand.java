package com.outbrain.pajamasproxy.memcached.server.protocol.command;

import java.util.concurrent.Future;

import com.outbrain.pajamasproxy.memcached.proxy.value.StoreResponse;
import com.outbrain.pajamasproxy.memcached.server.protocol.value.CommandMessage;
import io.netty.channel.Channel;

public class AsyncStoreCommand extends AbstractAsyncCommand<StoreResponse> {

  public AsyncStoreCommand(final CommandMessage command, final Channel channel, final Future<StoreResponse> futureResponse) {
    super(command, channel, futureResponse);
  }

  @Override
  protected void appendValueToResponse(final StoreResponse spyResponse) {
    responseMessage.withResponse(spyResponse);
  }

}
