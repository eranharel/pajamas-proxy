package com.thimbleware.jmemcached.protocol.binary;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.group.ChannelGroup;

import com.thimbleware.jmemcached.Cache;
import com.thimbleware.jmemcached.protocol.MemcachedCommandHandler;


public class MemcachedBinaryPipelineFactory implements ChannelPipelineFactory {

  private final MemcachedBinaryCommandDecoder decoder =  new MemcachedBinaryCommandDecoder();
  private final MemcachedCommandHandler memcachedCommandHandler;
  private final MemcachedBinaryResponseEncoder memcachedBinaryResponseEncoder = new MemcachedBinaryResponseEncoder();

  public MemcachedBinaryPipelineFactory(final Cache cache, final String version, final boolean verbose, final ChannelGroup channelGroup) {
    memcachedCommandHandler = new MemcachedCommandHandler(cache, version, verbose, channelGroup);
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
