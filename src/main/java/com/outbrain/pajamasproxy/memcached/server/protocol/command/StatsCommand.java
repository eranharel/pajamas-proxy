package com.outbrain.pajamasproxy.memcached.server.protocol.command;

import java.util.Map;
import java.util.Set;

import com.outbrain.pajamasproxy.memcached.server.protocol.value.CommandMessage;
import io.netty.channel.Channel;

public class StatsCommand extends SimpleCommand {

  public StatsCommand(final CommandMessage command, final Channel channel, final Map<String, Set<String>> stats) {
    super(command, channel);
    responseMessage.withStatResponse(stats);
  }

}
