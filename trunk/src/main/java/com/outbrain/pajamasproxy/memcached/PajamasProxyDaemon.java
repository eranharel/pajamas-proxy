package com.outbrain.pajamasproxy.memcached;

import java.net.InetSocketAddress;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.ServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.outbrain.pajamasproxy.memcached.protocol.ConnectionManager;

public class PajamasProxyDaemon {
  private static final Logger log = LoggerFactory.getLogger(PajamasProxyDaemon.class);

  private Channel inboundChannel;

  private final ServerBootstrap serverBootstrap;

  private final ServerSocketChannelFactory serverSocketChannelFactory;

  private final int localPort;

  private final ConnectionManager connectionManager;

  private transient boolean running = false;

  public PajamasProxyDaemon(final ServerBootstrap serverBootstrap, final ServerSocketChannelFactory serverSocketChannelFactory, final int localPort, final ConnectionManager connectionManager) {
    this.serverBootstrap = serverBootstrap;
    this.serverSocketChannelFactory = serverSocketChannelFactory;
    this.localPort = localPort;
    this.connectionManager = connectionManager;
  }

  public void start() {
    final InetSocketAddress socketAddress = new InetSocketAddress(localPort);
    inboundChannel = serverBootstrap.bind(socketAddress);

    running = true;
    log.info("Pajamas Proxy Daemon Server started on address {}", socketAddress);
  }

  public void stop() {
    log.info("Shutting down Pajamas Proxy Daemon Server");

    final ChannelFuture closeOpFuture = inboundChannel.close();
    closeOpFuture.awaitUninterruptibly();

    if (closeOpFuture.isSuccess()) {
      serverSocketChannelFactory.releaseExternalResources();
      log.info("Server shut down completed successfully");
    } else {
      log.error("Failed to close all network channels");
    }

    connectionManager.close();

    running = false;
  }

  public boolean isRunning() {
    return running;
  }

  public static void main(final String[] args) {
    final PajamasProxyDaemon daemon = new ClassPathXmlApplicationContext("ApplicationContext.xml").getBean(PajamasProxyDaemon.class);
    daemon.start();

    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
      @Override
      public void run() {
        if (daemon.isRunning()) {
          daemon.stop();
        }
      }
    }));
  }
}
