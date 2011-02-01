package com.outbrain.pajamasproxy.memcached.hash.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.outbrain.pajamasproxy.memcached.hash.HashAlgorithm;

public class KetamaHashAlgorithm implements HashAlgorithm {

  @Override
  public long hash(final byte[] key) {
    final byte[] bKey = md5(key);
    final long hash = (long) (bKey[3] & 0xFF) << 24 | (long) (bKey[2] & 0xFF) << 16 | (long) (bKey[1] & 0xFF) << 8 | bKey[0] & 0xFF;

    return hash & 0xffffffffL; // Truncate to 32-bits
  }

  private byte[] md5(final byte[] key) {
    MessageDigest md5;
    try {
      md5 = MessageDigest.getInstance("MD5");
    } catch (final NoSuchAlgorithmException e) {
      throw new RuntimeException("MD5 not supported", e);
    }
    md5.reset();
    md5.update(key);
    return md5.digest();
  }
}
