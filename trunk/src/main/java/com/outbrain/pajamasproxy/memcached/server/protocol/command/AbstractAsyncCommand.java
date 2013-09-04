package com.outbrain.pajamasproxy.memcached.server.protocol.command;

import java.util.concurrent.Future;

import com.outbrain.pajamasproxy.memcached.server.protocol.value.CommandMessage;
import io.netty.channel.Channel;

public abstract class AbstractAsyncCommand<V> extends AbstractCommand {

  protected final Future<V> futureResponse;

  public AbstractAsyncCommand(final CommandMessage command, final Channel channel, final Future<V> futureResponse) {
    super(command, channel);
    this.futureResponse = futureResponse;
  }

  @Override
  public void execute() throws InterruptedException {
    try {
      final V spyResponse = futureResponse.get();
      appendValueToResponse(spyResponse);
      channel.writeAndFlush(responseMessage);
    } catch (final InterruptedException e) {
      Thread.currentThread().interrupt();
      throw e;
    } catch (final Exception e) {
      log.error("failed to execute command", e);
      channel.pipeline().fireExceptionCaught(e);
    }
  }

  protected abstract void appendValueToResponse(V spyResponse);
}