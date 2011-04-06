package com.thimbleware.jmemcached;

import org.jboss.netty.buffer.ChannelBuffer;

/**
 * Represents a given key for lookup in the cache.
 *
 * Wraps a byte array with a precomputed hashCode.
 */
public class Key {
    public ChannelBuffer bytes;
    private final int hashCode;

    public Key(final ChannelBuffer bytes) {
        this.bytes = bytes.copy();
        this.hashCode = this.bytes.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
          return true;
        }
        if (o == null || getClass() != o.getClass()) {
          return false;
        }

        final Key key1 = (Key) o;

        bytes.readerIndex(0);
        key1.bytes.readerIndex(0);
        if (!bytes.equals(key1.bytes)) {
          return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }


}
