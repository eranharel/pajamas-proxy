package com.outbrain.pajamasproxy.memcached.server.protocol.binary;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import org.springframework.util.Assert;

import com.outbrain.pajamasproxy.memcached.server.protocol.MemcachedCommandHandler;


public class MemcachedBinaryPipelineFactory extends ChannelInitializer<Channel> {

//  private final MemcachedBinaryCommandDecoder decoder;
  private final MemcachedCommandHandler memcachedCommandHandler;
  private final MemcachedBinaryResponseEncoder memcachedBinaryResponseEncoder;

  public MemcachedBinaryPipelineFactory(final MemcachedCommandHandler memcachedCommandHandler, final MemcachedBinaryResponseEncoder memcachedBinaryResponseEncoder) {
//    Assert.notNull(decoder, "decoder may not be null");
    Assert.notNull(memcachedCommandHandler, "memcachedCommandHandler may not be null");
    Assert.notNull(memcachedBinaryResponseEncoder, "memcachedBinaryResponseEncoder may not be null");

//    this.decoder = decoder;
    this.memcachedCommandHandler = memcachedCommandHandler;
    this.memcachedBinaryResponseEncoder = memcachedBinaryResponseEncoder;
  }

  @Override
  protected void initChannel(Channel ch) throws Exception {
    ChannelPipeline pipeline = ch.pipeline();
    pipeline.addLast(new MemcachedBinaryCommandDecoder());
    pipeline.addLast(memcachedCommandHandler);
    pipeline.addLast(new MemcachedBinaryResponseEncoder());
  }
}
