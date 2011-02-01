package com.outbrain.pajamasproxy.memcached.protocol;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.ClientSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.outbrain.pajamasproxy.memcached.hash.impl.KetamaHashAlgorithm;
import com.outbrain.pajamasproxy.memcached.locator.ChannelLocator;
import com.outbrain.pajamasproxy.memcached.locator.impl.KetamaChannelLocator;


public class ConnectionManager {
  private static final Logger log = LoggerFactory.getLogger(ConnectionManager.class);

  private final ClientSocketChannelFactory clientSocketChannelFactory;

  private final List<InetSocketAddress> cluster;

  private final AtomicInteger openConnectionCount = new AtomicInteger();
  private final AtomicInteger totalConnectionCount = new AtomicInteger();

  private final Map<Channel, ChannelLocator> sessions = new ConcurrentHashMap<Channel, ChannelLocator>();

  public ConnectionManager(final ClientSocketChannelFactory clientSocketChannelFactory, final List<InetSocketAddress> cluster) {
    this.clientSocketChannelFactory = clientSocketChannelFactory;
    this.cluster = cluster;
  }

  public void connectToCluster(final Channel inboundChannel) {
    openConnectionCount.incrementAndGet();
    totalConnectionCount.incrementAndGet();

    // Suspend incoming traffic until connected to the remote hosts.
    inboundChannel.setReadable(false);

    // Start the connection attempt.
    final ClientBootstrap cb = new ClientBootstrap(clientSocketChannelFactory);
    cb.getPipeline().addLast("handler", new OutboundHandler(inboundChannel));

    final List<Channel> outboundChannels = new ArrayList<Channel>(cluster.size());
    final List<ChannelFuture> outboundChannelsFutures = new ArrayList<ChannelFuture>(cluster.size());

    for (final InetSocketAddress remoteServer : cluster) {
      log.info("connecting to remote server {}...", remoteServer);
      final ChannelFuture f = cb.connect(remoteServer);
      outboundChannels.add(f.getChannel());
      outboundChannelsFutures.add(f);
    }

    final WaitForAllChannelFutureListener waitForAllChannelFutureListener = new WaitForAllChannelFutureListener(inboundChannel, outboundChannels);

    for (final ChannelFuture channelFuture : outboundChannelsFutures) {
      channelFuture.addListener(waitForAllChannelFutureListener);
    }
  }

  public void clientDisconnected(final Channel inboundChannel) {
    final ChannelLocator outboundChannelLocator = sessions.remove(inboundChannel);

    if (outboundChannelLocator != null) {
      openConnectionCount.decrementAndGet();
      outboundChannelLocator.closeAll();
    }
  }

  public ChannelLocator getChannelLocator(final Channel inboundChannel) {
    return sessions.get(inboundChannel);
  }

  public void close() {
    for (final ChannelLocator session : sessions.values()) {
      session.closeAll();
    }
  }

  //  private void closeAll(final Collection<Channel> channels) {
  //    for (final Channel outboundChannel : channels) {
  //      ChannelUtil.closeOnFlush(outboundChannel);
  //      channelGroup.remove(outboundChannel);
  //    }
  //  }

  private class WaitForAllChannelFutureListener implements ChannelFutureListener {

    private final DefaultChannelGroup channelGroup = new DefaultChannelGroup();
    private final Channel inboundChannel;
    private final List<Channel> outboundChannels;
    private final AtomicInteger completedCount = new AtomicInteger();
    private final AtomicBoolean failed = new AtomicBoolean();

    public WaitForAllChannelFutureListener(final Channel inboundChannel, final List<Channel> outboundChannels) {
      this.inboundChannel = inboundChannel;
      this.outboundChannels = outboundChannels;
      channelGroup.add(inboundChannel);
      channelGroup.addAll(outboundChannels);
    }

    @Override
    public void operationComplete(final ChannelFuture future) throws Exception {
      if (future.isSuccess()) {
        log.info("connected to remote server {}", future.getChannel().getRemoteAddress());
      } else {
        failed.set(true);
        log.error("failed to connect to remote server: " + future.getChannel().getRemoteAddress(), future.getCause());
      }

      final int currConnectedCount = completedCount.incrementAndGet();
      if (currConnectedCount == outboundChannels.size()) {
        finalizeConnectProcess();
      }
    }

    private void finalizeConnectProcess() {
      if (failed.get()) {
        finalizeFailedConnection();
      } else {
        finalizeSuccessfullConnection();
      }
    }

    private void finalizeSuccessfullConnection() {
      sessions.put(inboundChannel, new KetamaChannelLocator(new KetamaHashAlgorithm(), inboundChannel, outboundChannels));
      inboundChannel.setReadable(true);
    }

    private void finalizeFailedConnection() {
      channelGroup.close();
    }
  }
}
