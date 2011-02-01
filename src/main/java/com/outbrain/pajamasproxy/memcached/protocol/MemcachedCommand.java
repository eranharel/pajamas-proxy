package com.outbrain.pajamasproxy.memcached.protocol;

import org.jboss.netty.buffer.ChannelBuffer;

public class MemcachedCommand {
  private final byte opcode;
  private final byte[] key;
  private final ChannelBuffer messageBuffer;

  public MemcachedCommand(final ChannelBuffer messageBuffer, final byte[] key, final byte opcode) {
    this.messageBuffer = messageBuffer;
    this.key = key;
    this.opcode = opcode;
  }

  public byte getOpcode() {
    return opcode;
  }

  public byte[] getKey() {
    return key;
  }

  public ChannelBuffer getMessageBuffer() {
    return messageBuffer;
  }
}
