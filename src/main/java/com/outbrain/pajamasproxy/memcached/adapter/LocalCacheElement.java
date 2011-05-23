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
package com.outbrain.pajamasproxy.memcached.adapter;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;


/**
 * Represents information about a cache entry.
 */
public final class LocalCacheElement implements CacheElement, Externalizable {
  private int expire ;
  private int flags;
  private ChannelBuffer data;
  private Key key;
  private long casUnique = 0L;

  public LocalCacheElement(final Key key) {
    this.key = key;
  }

  public LocalCacheElement(final Key key, final int flags, final int expire, final long casUnique) {
    this.key = key;
    this.flags = flags;
    this.expire = expire;
    this.casUnique = casUnique;
  }

  /**
   * @return the current time in seconds
   */
  public static int Now() {
    return (int) (System.currentTimeMillis() / 1000);
  }

  @Override
  public int size() {
    return getData().capacity();
  }

  public static class IncrDecrResult {
    int oldValue;
    LocalCacheElement replace;

    public IncrDecrResult(final int oldValue, final LocalCacheElement replace) {
      this.oldValue = oldValue;
      this.replace = replace;
    }
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    final LocalCacheElement that = (LocalCacheElement) o;

    if (casUnique != that.casUnique) {
      return false;
    }
    if (expire != that.expire) {
      return false;
    }
    if (flags != that.flags) {
      return false;
    }
    if (!data.equals(that.data)) {
      return false;
    }
    if (!key.equals(that.key)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = expire;
    result = 31 * result + flags;
    result = 31 * result + (data != null ? data.hashCode() : 0);
    result = 31 * result + key.hashCode();
    result = 31 * result + (int) (casUnique ^ (casUnique >>> 32));
    return result;
  }

  public static LocalCacheElement key(final Key key) {
    return new LocalCacheElement(key);
  }

  @Override
  public int getExpire() {
    return expire;
  }

  @Override
  public int getFlags() {
    return flags;
  }

  @Override
  public ChannelBuffer getData() {
    data.readerIndex(0);
    return data;
  }

  @Override
  public Key getKey() {
    return key;
  }

  @Override
  public long getCasUnique() {
    return casUnique;
  }

  @Override
  public void setCasUnique(final long casUnique) {
    this.casUnique = casUnique;
  }

  @Override
  public void setData(final ChannelBuffer data) {
    data.readerIndex(0);
    this.data = data;
  }

  @Override
  public void readExternal(final ObjectInput in) throws IOException{
    expire = in.readInt() ;
    flags = in.readInt();

    final int length = in.readInt();
    int readSize = 0;
    final byte[] dataArrary = new byte[length];
    while( readSize < length) {
      readSize += in.read(dataArrary, readSize, length - readSize);
    }
    data = ChannelBuffers.wrappedBuffer(dataArrary);


    final byte[] keyBytes = new byte[in.readInt()];
    in.read(keyBytes);
    key = new Key(ChannelBuffers.wrappedBuffer(keyBytes));
    casUnique = in.readLong();
  }

  @Override
  public void writeExternal(final ObjectOutput out) throws IOException {
    out.writeInt(expire) ;
    out.writeInt(flags);
    final byte[] dataArray = data.copy().array();
    out.writeInt(dataArray.length);
    out.write(dataArray);
    out.write(key.bytes.capacity());
    out.write(key.bytes.copy().array());
    out.writeLong(casUnique);
  }
}