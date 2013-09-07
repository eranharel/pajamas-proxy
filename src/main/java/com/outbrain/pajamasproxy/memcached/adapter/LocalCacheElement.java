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

import io.netty.buffer.ByteBuf;

/**
 * Represents information about a cache entry.
 */
public final class LocalCacheElement implements CacheElement {
  private int expire;
  private int flags;
  private ByteBuf data;
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

  public static LocalCacheElement key(final Key key) {
    return new LocalCacheElement(key);
  }

  @Override
  public int size() {
    return getData().capacity();
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

    return key.equals(that.key);
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

  @Override
  public int getExpire() {
    return expire;
  }

  @Override
  public int getFlags() {
    return flags;
  }

  @Override
  public ByteBuf getData() {
    data.readerIndex(0);
    return data;
  }

  @Override
  public void setData(final ByteBuf data) {
    data.readerIndex(0);
    this.data = data;
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

}
