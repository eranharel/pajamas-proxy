package com.outbrain.pajamasproxy.memcached.server.protocol.command;

import com.outbrain.pajamasproxy.memcached.server.protocol.value.CommandMessage;
import io.netty.channel.Channel;

public class VersionCommand extends SimpleCommand {

  public VersionCommand(final CommandMessage command, final Channel channel, final String version) {
    super(command, channel);
    responseMessage.version = version;
  }

}
