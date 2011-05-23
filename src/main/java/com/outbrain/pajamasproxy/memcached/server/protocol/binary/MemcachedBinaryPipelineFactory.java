package com.outbrain.pajamasproxy.memcached.server.protocol.binary;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.springframework.util.Assert;

import com.outbrain.pajamasproxy.memcached.server.protocol.MemcachedCommandHandler;


public class MemcachedBinaryPipelineFactory implements ChannelPipelineFactory {

  private final MemcachedBinaryCommandDecoder decoder;
  private final MemcachedCommandHandler memcachedCommandHandler;
  private final MemcachedBinaryResponseEncoder memcachedBinaryResponseEncoder;

  public MemcachedBinaryPipelineFactory(final MemcachedBinaryCommandDecoder decoder, final MemcachedCommandHandler memcachedCommandHandler, final MemcachedBinaryResponseEncoder memcachedBinaryResponseEncoder) {
    Assert.notNull(decoder, "decoder may not be null");
    Assert.notNull(memcachedCommandHandler, "memcachedCommandHandler may not be null");
    Assert.notNull(memcachedBinaryResponseEncoder, "memcachedBinaryResponseEncoder may not be null");

    this.decoder = decoder;
    this.memcachedCommandHandler = memcachedCommandHandler;
    this.memcachedBinaryResponseEncoder = memcachedBinaryResponseEncoder;
  }

  @Override
  public ChannelPipeline getPipeline() throws Exception {
    return Channels.pipeline(
        decoder,
        memcachedCommandHandler,
        memcachedBinaryResponseEncoder
    );
  }
}
