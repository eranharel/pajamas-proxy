package com.outbrain.pajamasproxy.memcached.server.protocol.command;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.outbrain.pajamasproxy.memcached.server.protocol.value.CommandMessage;
import com.outbrain.pajamasproxy.memcached.server.protocol.value.ResponseMessage;

public abstract class AbstractCommand implements Command {

  protected final Logger log = LoggerFactory.getLogger(getClass());

  protected final ChannelHandlerContext channelHandlerContext;
  protected final ResponseMessage responseMessage;
  protected final Channel channel;

  public AbstractCommand(final ChannelHandlerContext channelHandlerContext, final CommandMessage command, final Channel channel) {
    this.channelHandlerContext = channelHandlerContext;
    this.responseMessage = new ResponseMessage(command);
    this.channel = channel;
  }
}