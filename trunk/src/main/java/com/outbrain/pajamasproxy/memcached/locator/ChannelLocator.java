package com.outbrain.pajamasproxy.memcached.locator;

import org.jboss.netty.channel.Channel;

/**
 * A channel locator API - locates the channel for a given key
 * @author Eran Harel
 */
public interface ChannelLocator {

  /**
   * @param key a memcached command (e.g. get) key
   * @return the resolved channel for the specified key
   */
  public Channel getChannel(byte[] key);

  public void closeAll();
}
