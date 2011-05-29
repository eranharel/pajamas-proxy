package com.outbrain.pajamasproxy.memcached.server.protocol.command;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


class CommandQueueImpl implements CommandQueue, CommandPoller {

  private static final Logger log = LoggerFactory.getLogger(CommandQueueImpl.class);

  private final BlockingQueue<ProfiledCommand> q = new LinkedBlockingQueue<ProfiledCommand>();

  /* (non-Javadoc)
   * @see com.thimbleware.jmemcached.CommandQueue#enqueueFutureResponse(com.thimbleware.jmemcached.command.AsyncCommand)
   */
  @Override
  public void enqueueFutureResponse(final Command command) {
    q.add(new ProfiledCommand(command));
  }

  /* (non-Javadoc)
   * @see com.outbrain.pajamasproxy.memcached.command.CommandPoller#run()
   */
  @Override
  public void run() {
    // TODO we may need a more delicate error handling...
    // timeouts propagate as RuntimeExceptions
    while (true) {
      ProfiledCommand profiledCommand = null;
      try {
        profiledCommand = q.take();
        log.debug("executing {} command", profiledCommand.command.getClass());
        profiledCommand.command.execute();
      } catch (final InterruptedException e) {
        Thread.currentThread().interrupt();
        break;
      } catch (final Exception e) {
        log.error("Failed to execute command. We shouldn't get here...", e);
      } finally {
        if (profiledCommand != null) {
          profiledCommand.stopWatch.stop();
        }
      }
    }
  }

  private static class ProfiledCommand {
    private final Command command;
    private final StopWatch stopWatch;

    public ProfiledCommand(final Command command) {
      this.command = command;
      this.stopWatch = new Slf4JStopWatch(command.getClass().getName());
    }
  }
}
