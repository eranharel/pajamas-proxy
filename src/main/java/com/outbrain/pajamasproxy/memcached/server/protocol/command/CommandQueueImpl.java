package com.outbrain.pajamasproxy.memcached.server.protocol.command;

import java.util.concurrent.Executors;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

class CommandQueueImpl implements CommandQueue, EventHandler<CommandQueueImpl.ProfiledCommand> {

  private static final Logger log = LoggerFactory.getLogger(CommandQueueImpl.class);
  private final RingBuffer<ProfiledCommand> ringBuffer;
  private final MetricRegistry metrics;

  public CommandQueueImpl(MetricRegistry metrics) {
    this.metrics = metrics;

    Disruptor<ProfiledCommand> disruptor = new Disruptor<ProfiledCommand>(new ProfiledCommandFactory(), 16384, Executors.newSingleThreadExecutor(),
        ProducerType.MULTI, new SleepingWaitStrategy());
    disruptor.handleEventsWith(this);
    ringBuffer = disruptor.start();
  }

  @Override
  public void enqueueFutureResponse(final Command command) {
    long seq = ringBuffer.next();
    ProfiledCommand profiledCommand = ringBuffer.get(seq);
    profiledCommand.setCommand(command, metrics);
    ringBuffer.publish(seq);
  }

  @Override
  public void onEvent(ProfiledCommand event, long sequence, boolean endOfBatch) throws Exception {
    log.debug("executing {} command", event.command.getClass());
    try {
      event.command.execute();
    } finally {
      event.stopWatch.stop();
    }
  }

  public static class ProfiledCommand {
    private Command command;
    private Timer.Context stopWatch;

    public void setCommand(final Command command, MetricRegistry metrics) {
      this.command = command;
      this.stopWatch = metrics.timer(command.getClass().getSimpleName()).time();
    }
  }

  private static class ProfiledCommandFactory implements EventFactory<ProfiledCommand> {
    public ProfiledCommand newInstance() {
      return new ProfiledCommand();
    }
  }
}
