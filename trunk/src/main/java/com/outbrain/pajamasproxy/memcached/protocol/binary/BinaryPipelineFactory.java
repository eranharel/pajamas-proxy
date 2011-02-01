package com.outbrain.pajamasproxy.memcached.protocol.binary;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;

import com.outbrain.pajamasproxy.memcached.protocol.MemcachedCommandHandler;

public class BinaryPipelineFactory implements ChannelPipelineFactory {

  private final BinaryCommandDecoder decoder;
  private final MemcachedCommandHandler commandHandler;

  public BinaryPipelineFactory(final BinaryCommandDecoder decoder, final MemcachedCommandHandler commandHandler) {
    super();
    this.decoder = decoder;
    this.commandHandler = commandHandler;
  }

  @Override
  public ChannelPipeline getPipeline() throws Exception {
    return Channels.pipeline(decoder, commandHandler);
  }

}
