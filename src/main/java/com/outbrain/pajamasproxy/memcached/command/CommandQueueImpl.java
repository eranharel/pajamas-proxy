package com.outbrain.pajamasproxy.memcached.command;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


class CommandQueueImpl implements CommandQueue, CommandPoller {

  private static final Logger log = LoggerFactory.getLogger(CommandQueueImpl.class);

  private final BlockingQueue<Command> q = new LinkedBlockingQueue<Command>();

  /* (non-Javadoc)
   * @see com.thimbleware.jmemcached.CommandQueue#enqueueFutureResponse(com.thimbleware.jmemcached.command.AsyncCommand)
   */
  @Override
  public void enqueueFutureResponse(final Command command) {
    q.add(command);
  }

  /* (non-Javadoc)
   * @see com.outbrain.pajamasproxy.memcached.command.CommandPoller#run()
   */
  @Override
  public void run() {
    // TODO we may need a more delicate error handling...
    // timeouts propagate as RuntimeExceptions
    while (true) {
      try {
        final Command command = q.take();
        log.debug("executing {} command", command.getClass());
        command.execute();
      } catch (final InterruptedException e) {
        Thread.currentThread().interrupt();
        break;
      } catch (final Exception e) {
        log.error("Failed to execute command. We shouldn't get here...", e);
      }
    }
  }

}
