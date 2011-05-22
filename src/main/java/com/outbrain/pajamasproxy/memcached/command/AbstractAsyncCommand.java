package com.outbrain.pajamasproxy.memcached.command;

import java.util.concurrent.Future;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;

import com.thimbleware.jmemcached.protocol.CommandMessage;

public abstract class AbstractAsyncCommand<V> extends AbstractCommand {

  protected final Future<V> futureResponse;

  public AbstractAsyncCommand(final ChannelHandlerContext channelHandlerContext, final CommandMessage command, final Channel channel, final Future<V> futureResponse) {
    super(channelHandlerContext, command, channel);
    this.futureResponse = futureResponse;
  }

  /* (non-Javadoc)
   * @see com.outbrain.pajamasproxy.memcached.command.AsyncCommand#execute()
   */
  @Override
  public void execute() throws InterruptedException {
    try {
      final V spyResponse = futureResponse.get();
      appendValueToResponse(spyResponse);
      Channels.fireMessageReceived(channelHandlerContext, responseMessage, channel.getRemoteAddress());
    } catch (final InterruptedException e) {
      Thread.currentThread().interrupt();
      throw e;
    } catch (final Exception e) {
      log.error("failed to execute command", e);
      Channels.fireExceptionCaught(channel, e);
    }
  }

  protected abstract void appendValueToResponse(V spyResponse);
}