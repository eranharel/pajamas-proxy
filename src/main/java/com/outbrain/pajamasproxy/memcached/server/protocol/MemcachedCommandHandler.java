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
package com.outbrain.pajamasproxy.memcached.server.protocol;

import java.util.Collections;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import com.outbrain.pajamasproxy.memcached.adapter.Key;
import com.outbrain.pajamasproxy.memcached.server.protocol.command.AsyncGetMultiCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.outbrain.pajamasproxy.memcached.adapter.CacheElement;
import com.outbrain.pajamasproxy.memcached.proxy.AsyncCache;
import com.outbrain.pajamasproxy.memcached.proxy.value.DeleteResponse;
import com.outbrain.pajamasproxy.memcached.proxy.value.StoreResponse;
import com.outbrain.pajamasproxy.memcached.server.protocol.command.AsyncDeleteCommand;
import com.outbrain.pajamasproxy.memcached.server.protocol.command.AsyncFlushCommand;
import com.outbrain.pajamasproxy.memcached.server.protocol.command.AsyncGetCommand;
import com.outbrain.pajamasproxy.memcached.server.protocol.command.AsyncMutateCommand;
import com.outbrain.pajamasproxy.memcached.server.protocol.command.AsyncStoreCommand;
import com.outbrain.pajamasproxy.memcached.server.protocol.command.CommandQueue;
import com.outbrain.pajamasproxy.memcached.server.protocol.command.SimpleCommand;
import com.outbrain.pajamasproxy.memcached.server.protocol.command.StatsCommand;
import com.outbrain.pajamasproxy.memcached.server.protocol.command.VersionCommand;
import com.outbrain.pajamasproxy.memcached.server.protocol.exceptions.UnknownCommandException;
import com.outbrain.pajamasproxy.memcached.server.protocol.value.CommandMessage;
import com.outbrain.pajamasproxy.memcached.server.protocol.value.Op;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

// TODO implement flush_all delay

/**
 * The actual command handler, which is responsible for processing the CommandMessage instances
 * that are inbound from the protocol decoders.
 * <p/>
 * One instance is shared among the entire pipeline, since this handler is stateless, apart from some globals
 * for the entire daemon.
 * <p/>
 * The command handler produces ResponseMessages which are destined for the response encoder.
 */
@ChannelHandler.Sharable
public final class MemcachedCommandHandler extends SimpleChannelInboundHandler<CommandMessage> implements ServerConnectionStatistics {

  final Logger logger = LoggerFactory.getLogger(MemcachedCommandHandler.class);
  private final AtomicInteger currentConnectionCount = new AtomicInteger();
  private final AtomicInteger totalConnectionCount = new AtomicInteger();
  /**
   * The following state variables are universal for the entire daemon. These are used for statistics gathering.
   * In order for these values to work properly, the handler _must_ be declared with a ChannelPipelineCoverage
   * of "all".
   */
  private final String version;
  private final boolean verbose;
  /**
   * The actual physical data storage.
   */
  private final AsyncCache cache;
  private final CommandQueue commandQueue;

  /**
   * Construct the server session handler
   *
   * @param cache            the cache to use
   * @param memcachedVersion the version string to return to clients
   * @param verbosity        verbosity level for debugging
   */
  public MemcachedCommandHandler(final AsyncCache cache, final String memcachedVersion, final boolean verbosity, final CommandQueue commandQueue) {
    this.cache = cache;
    this.version = memcachedVersion;
    this.verbose = verbosity;
    this.commandQueue = commandQueue;
  }

  @Override
  public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
    totalConnectionCount.incrementAndGet();
    currentConnectionCount.incrementAndGet();
  }

  @Override
  public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
    currentConnectionCount.decrementAndGet();
  }

  /**
   * The actual meat of the matter.  Turn CommandMessages into executions against the physical cache, and then
   * pass on the downstream messages.
   */
  protected void channelRead0(ChannelHandlerContext channelHandlerContext, CommandMessage command) throws Exception {

    final Op cmd = command.op;

    // now do the real work
    if (this.verbose) {
      final StringBuilder log = new StringBuilder(1024);
      log.append(cmd);
      if (command.element != null) {
        log.append(" ").append(command.element.getKey());
      }
      log.append(" ").append(command.key);
      logger.info(log.toString());
    }

    final Channel channel = channelHandlerContext.channel();
    if (cmd == null) {
      handleNoOp(command, channel);
    } else {
      switch (cmd) {
      case GET:
        handleGet(command, channel);
        break;
      case GETS:
        handleGets(command, channel);
        break;
      case APPEND:
        handleAppend(command, channel);
        break;
      case PREPEND:
        handlePrepend(command, channel);
        break;
      case DELETE:
        handleDelete(command, channel);
        break;
      case DECR:
        handleDecr(command, channel);
        break;
      case INCR:
        handleIncr(command, channel);
        break;
      case REPLACE:
        handleReplace(command, channel);
        break;
      case ADD:
        handleAdd(command, channel);
        break;
      case SET:
        handleSet(command, channel);
        break;
      case CAS:
        handleCas(command, channel);
        break;
      case STATS:
        handleStats(command, channel);
        break;
      case VERSION:
        handleVersion(command, channel);
        break;
      case QUIT:
        handleQuit(channel);
        break;
      case FLUSH_ALL:
        handleFlush(command, channel);
        break;
      case VERBOSITY:
        handleVerbosity(command, channel);
        break;
      default:
        throw new UnknownCommandException("unknown command");
      }
    }
  }

  protected void handleNoOp(final CommandMessage command, final Channel channel) {
    commandQueue.enqueueFutureResponse(new SimpleCommand(command, channel));
  }

  protected void handleFlush(final CommandMessage command, final Channel channel) {
    final Future<Boolean> futureResponse = cache.flushAll();
    commandQueue.enqueueFutureResponse(new AsyncFlushCommand(command, channel, futureResponse));
  }

  protected void handleVerbosity(final CommandMessage command, final Channel channel) {
    //TODO set verbosity mode
    commandQueue.enqueueFutureResponse(new SimpleCommand(command, channel));
  }

  protected void handleQuit(final Channel channel) {
    channel.disconnect();
  }

  protected void handleVersion(final CommandMessage command, final Channel channel) {
    commandQueue.enqueueFutureResponse(new VersionCommand(command, channel, version));
  }

  protected void handleStats(final CommandMessage command, final Channel channel) {
    String option = "";
    if (command.key != null) {
      option = command.key.bytes.toString();
    }

    commandQueue.enqueueFutureResponse(new StatsCommand(command, channel, cache.stats(option)));
  }

  protected void handleDelete(final CommandMessage command, final Channel channel) {
    final Future<DeleteResponse> futureResponse = cache.delete(command.key);
    commandQueue.enqueueFutureResponse(new AsyncDeleteCommand(command, channel, futureResponse));
  }

  protected void handleDecr(final CommandMessage command, final Channel channel) {
    final Future<Long> futureResponse = cache.decrement(command.key, command.incrAmount);
    commandQueue.enqueueFutureResponse(new AsyncMutateCommand(command, channel, futureResponse));
  }

  protected void handleIncr(final CommandMessage command, final Channel channel) {
    final Future<Long> futureResponse = cache.increment(command.key, command.incrAmount);
    commandQueue.enqueueFutureResponse(new AsyncMutateCommand(command, channel, futureResponse));
  }

  protected void handlePrepend(final CommandMessage command, final Channel channel) {
    final Future<StoreResponse> futureResponse = cache.prepend(command.element);
    commandQueue.enqueueFutureResponse(new AsyncStoreCommand(command, channel, futureResponse));
  }

  protected void handleAppend(final CommandMessage command, final Channel channel) {
    final Future<StoreResponse> futureResponse = cache.append(command.element);
    commandQueue.enqueueFutureResponse(new AsyncStoreCommand(command, channel, futureResponse));
  }

  protected void handleReplace(final CommandMessage command, final Channel channel) {
    final Future<StoreResponse> futureResponse = cache.replace(command.element);
    commandQueue.enqueueFutureResponse(new AsyncStoreCommand(command, channel, futureResponse));
  }

  protected void handleAdd(final CommandMessage command, final Channel channel) {
    final Future<StoreResponse> futureResponse = cache.add(command.element);
    commandQueue.enqueueFutureResponse(new AsyncStoreCommand(command, channel, futureResponse));
  }

  protected void handleCas(final CommandMessage command, final Channel channel) {
    final Future<StoreResponse> futureResponse = cache.cas(command.cas_key, command.element);
    commandQueue.enqueueFutureResponse(new AsyncStoreCommand(command, channel, futureResponse));
  }

  protected void handleSet(final CommandMessage command, final Channel channel) {
    final Future<StoreResponse> futureResponse = cache.set(command.element);
    commandQueue.enqueueFutureResponse(new AsyncStoreCommand(command, channel, futureResponse));
  }

  protected void handleGet(final CommandMessage command, final Channel channel) {
    final Future<CacheElement> futureResponse = cache.get(command.key);
    commandQueue.enqueueFutureResponse(new AsyncGetCommand(command, channel, futureResponse));
  }

  protected void handleGets(final CommandMessage command, final Channel channel) {
    // TODO we never really get here...
    final Future<CacheElement[]> futureResponse = cache.get(Collections.<Key>singleton(command.key));
    commandQueue.enqueueFutureResponse(new AsyncGetMultiCommand(command, channel, futureResponse));
  }

  @Override
  public int getCurrentConnectionCount() {
    return currentConnectionCount.get();
  }

  @Override
  public int getTotalConnectionCount() {
    return totalConnectionCount.get();
  }
}
