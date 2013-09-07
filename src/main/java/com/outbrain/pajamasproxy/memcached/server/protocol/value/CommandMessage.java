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
package com.outbrain.pajamasproxy.memcached.server.protocol.value;

import java.util.ArrayList;
import java.util.List;

import com.outbrain.pajamasproxy.memcached.adapter.CacheElement;
import com.outbrain.pajamasproxy.memcached.adapter.Key;
import io.netty.buffer.ByteBuf;

/**
 * The payload object holding the parsed message.
 */
public final class CommandMessage {

  public Op op;
  public CacheElement element;
  public Key key;
  public boolean noreply;
  public long cas_key;
  public int time = 0;
  public int opaque;
  public boolean addKeyToResponse = false;
  public int incrExpiry;
  public int incrAmount;

  private CommandMessage(final Op op) {
    this.op = op;
    element = null;
  }

  public static CommandMessage command(final Op operation) {
    return new CommandMessage(operation);
  }

}
