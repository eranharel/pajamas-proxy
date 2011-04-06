/**
 *  Copyright 2008 ThimbleWare Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.thimbleware.jmemcached;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.socket.ServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The actual daemon - responsible for the binding and configuration of the network configuration.
 */
public class MemCacheDaemon<CACHE_ELEMENT extends CacheElement> {

  final Logger log = LoggerFactory.getLogger(MemCacheDaemon.class);

  public static String memcachedVersion = "0.9";

  private InetSocketAddress addr;
  private final Cache cache;

  private boolean running = false;
  private final ServerSocketChannelFactory channelFactory;
  private final ChannelGroup allChannels;

  private final ServerBootstrap serverBootstrap;

  public MemCacheDaemon(final Cache cache, final ServerSocketChannelFactory channelFactory, final ChannelGroup allChannels, final ServerBootstrap serverBootstrap) {
    this.cache = cache;
    this.channelFactory = channelFactory;
    this.allChannels = allChannels;
    this.serverBootstrap = serverBootstrap;
  }

  /**
   * Bind the network connection and start the network processing threads.
   */
  public void start() {

    final Channel serverChannel = serverBootstrap.bind(addr);
    allChannels.add(serverChannel);

    log.info("Listening on " + String.valueOf(addr.getHostName()) + ":" + addr.getPort());

    running = true;
  }

  public void stop() {
    log.info("terminating daemon; closing all channels");

    final ChannelGroupFuture future = allChannels.close();
    future.awaitUninterruptibly();
    if (!future.isCompleteSuccess()) {
      throw new RuntimeException("failure to complete closing all network channels");
    }

    log.info("channels closed, closing cache");
    try {
      cache.close();
    } catch (final IOException e) {
      throw new RuntimeException("exception while closing storage", e);
    }

    channelFactory.releaseExternalResources();

    running = false;
    log.info("successfully shut down");
  }

  public void setAddr(final InetSocketAddress addr) {
    this.addr = addr;
  }

  public boolean isRunning() {
    return running;
  }

}
