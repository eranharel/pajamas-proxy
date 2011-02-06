package com.outbrain.pajamasproxy.memcached.protocol;


public class MemcachedCommand {
  private final byte opcode;
  private final String key;
  private final byte[] value;

  public MemcachedCommand(final String key, final byte[] value, final byte opcode) {
    this.key = key;
    this.value = value;
    this.opcode = opcode;
  }

  public byte getOpcode() {
    return opcode;
  }

  public String getKey() {
    return key;
  }

  public byte[] getValue() {
    return value;
  }
}
