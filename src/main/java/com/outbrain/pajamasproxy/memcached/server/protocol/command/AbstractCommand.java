package com.outbrain.pajamasproxy.memcached.server.protocol.command;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.outbrain.pajamasproxy.memcached.server.protocol.value.CommandMessage;
import com.outbrain.pajamasproxy.memcached.server.protocol.value.ResponseMessage;

public abstract class AbstractCommand implements Command {

  protected final Logger log = LoggerFactory.getLogger(getClass());

  protected final ResponseMessage responseMessage;
  protected final Channel channel;

  public AbstractCommand(final CommandMessage command, final Channel channel) {
    this.responseMessage = new ResponseMessage(command);
    this.channel = channel;
  }
}