package com.outbrain.pajamasproxy.memcached.protocol;

import net.rubyeye.xmemcached.MemcachedClient;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public class MemcachedCommandHandler extends SimpleChannelUpstreamHandler {

  private static final Logger log = LoggerFactory.getLogger(MemcachedCommandHandler.class);

  private final MemcachedClient memcachedClient;

  public MemcachedCommandHandler(final MemcachedClient memcachedClient) {
    Assert.notNull(memcachedClient, "memcachedClient may not be null");
    this.memcachedClient = memcachedClient;
  }

  @Override
  public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
    if (!(e.getMessage() instanceof MemcachedCommand)) {
      // can't handle - ignore.
      ctx.sendUpstream(e);
      return;
    }

    final MemcachedCommand command = (MemcachedCommand) e.getMessage();

    log.debug("handling command, opcode={} key={} value={}", new Object[] { command.getOpcode(), command.getKey(), command.getValue() });

    switch (command.getOpcode()) {
    case 0x00: // get
      if (null != command.getKey()) {
        final byte[] value = memcachedClient.get(command.getKey());
        log.debug("returning {}", value);
        e.getChannel().write(generateGetResponse(value));
      }
      break;
    case 0x01: // Set
      memcachedClient.set(command.getKey(), 0, command.getValue());
      e.getChannel().write(generateSetResponse());
      break;
    case 0x08: // Flush
      memcachedClient.flushAll();
      e.getChannel().write(generateFlushAllResponse());
      break;
    case 0x07: // quit
      e.getChannel().write(generateOpResponse((byte) 7));
      ChannelUtil.closeOnFlush(e.getChannel());
      break;
      /*      0x00    Get
          0x01    Set
          0x02    Add
          0x03    Replace
          0x04    Delete
          0x05    Increment
          0x06    Decrement
          0x07    Quit
          0x08    Flush
          0x09    GetQ
          0x0A    No-op
          0x0B    Version
          0x0C    GetK
          0x0D    GetKQ
          0x0E    Append
          0x0F    Prepend
          0x10    Stat
          0x11    SetQ
          0x12    AddQ
          0x13    ReplaceQ
          0x14    DeleteQ
          0x15    IncrementQ
          0x16    DecrementQ
          0x17    QuitQ
          0x18    FlushQ
          0x19    AppendQ
          0x1A    PrependQ
       */
    default:
      return;
    }
  }

  private ChannelBuffer generateGetResponse(final byte[] value) {
    /*
         Byte/     0       |       1       |       2       |       3       |
            /              |               |               |               |
           |0 1 2 3 4 5 6 7|0 1 2 3 4 5 6 7|0 1 2 3 4 5 6 7|0 1 2 3 4 5 6 7|
           +---------------+---------------+---------------+---------------+
          0| Magic         | Opcode        | Key Length                    |
           +---------------+---------------+---------------+---------------+
          4| Extras length | Data type     | Status                        |
           +---------------+---------------+---------------+---------------+
          8| Total body length                                             |
           +---------------+---------------+---------------+---------------+
         12| Opaque                                                        |
           +---------------+---------------+---------------+---------------+
         16| CAS                                                           |
           |                                                               |
           +---------------+---------------+---------------+---------------+
           Total 24 bytes
     */
    final int valueLength = value == null ? 0 : value.length;
    final ChannelBuffer response = ChannelBuffers.buffer(24 + 4 + valueLength);

    response.writeByte((byte) 0x81); // magic
    response.writeByte(0); // get opcode
    response.writeShort(0); // key lenght
    response.writeByte(4); // extras length
    response.writeByte(0); // data type (unused)
    response.writeShort(0); // status code (success)
    response.writeInt(4 + valueLength); // total body length
    response.writeInt(0); // opaque
    response.writeLong(1); // cas???
    response.writeInt(0); // extras
    if (null != value) {
      response.writeBytes(value);
    }

    log.debug("QQQQQQ {}", response.array());

    return response;
  }

  private ChannelBuffer generateSetResponse() {
    return generateOpResponse((byte) 1);
  }

  private ChannelBuffer generateFlushAllResponse() {
    return generateOpResponse((byte) 8);
  }

  private ChannelBuffer generateOpResponse(final byte opcode) {
    final ChannelBuffer response = ChannelBuffers.buffer(24);
    response.writeByte((byte) 0x81); // magic
    response.writeByte(opcode);
    response.writeShort(0); // key lenght
    response.writeByte(0); // extras length
    response.writeByte(0); // data type (unused)
    response.writeShort(0); // status code (success)
    response.writeInt(0); // total body length
    response.writeInt(0); // opaque
    response.writeLong(1); // cas???
    return response;
  }
}
