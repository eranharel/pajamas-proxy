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
package com.outbrain.pajamasproxy.memcached.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.outbrain.pajamasproxy.memcached.adapter.CacheElement;
import com.outbrain.pajamasproxy.memcached.proxy.AsyncCache;

/**
 * The actual daemon - responsible for the binding and configuration of the network configuration.
 */
public class MemCacheDaemon {

  final Logger log = LoggerFactory.getLogger(MemCacheDaemon.class);

  private InetSocketAddress addr;

  private final EventLoopGroup eventLoopGroup;
  private final ChannelHandler serverPipelineFactory;

  private boolean running = false;

  public MemCacheDaemon(EventLoopGroup eventLoopGroup, ChannelHandler serverPipelineFactory) {
    this.eventLoopGroup = eventLoopGroup;
    this.serverPipelineFactory = serverPipelineFactory;
  }

  /**
   * Bind the network connection and start the network processing threads.
   */
  public void start() {
    createBootstratp();
  }

  private void createBootstratp() {
    log.info("Initializing TCP...");
    ServerBootstrap tcpBootstrap = new ServerBootstrap();
    tcpBootstrap.group(eventLoopGroup);
    tcpBootstrap.channel(NioServerSocketChannel.class);
    tcpBootstrap.childHandler(serverPipelineFactory);

    final ChannelFuture channelFuture = tcpBootstrap.bind(addr).addListener(new ChannelFutureListener() {
      @Override
      public void operationComplete(ChannelFuture future) throws Exception {
        log.info("Server started; listening to {}", addr);
        running = true;
      }
    });
  }

  public void stop() {
    log.info("terminating daemon;");
    eventLoopGroup.shutdownGracefully(1, 5, TimeUnit.SECONDS);
    log.info("successfully shut down");
  }

  public void setAddr(final InetSocketAddress addr) {
    this.addr = addr;
  }

  public boolean isRunning() {
    return running;
  }

}
