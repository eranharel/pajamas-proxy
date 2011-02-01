package com.outbrain.pajamasproxy.memcached.hash;

/**
 * Defines an API for a hashing algorithm used for locating a server for a key.
 * Note that all hash algorithms should return 64-bits of hash, but only the lower 32-bits are
 * significant. This allows a positive 32-bit number to be returned for all cases.
 */
public interface HashAlgorithm {

  /**
   * @return A positive 64 bit hash code for the given key.
   */
  public long hash(final byte[] key);
}
