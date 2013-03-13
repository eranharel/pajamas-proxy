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


/**
 */
public enum Op {
  GET, GETS, APPEND, PREPEND, DELETE, DECR,
  INCR, REPLACE, ADD, SET, CAS, STATS, VERSION,
  QUIT, FLUSH_ALL, VERBOSITY;
}
