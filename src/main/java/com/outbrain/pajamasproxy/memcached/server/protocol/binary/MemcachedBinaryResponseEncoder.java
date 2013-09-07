package com.outbrain.pajamasproxy.memcached.server.protocol.binary;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.outbrain.pajamasproxy.memcached.adapter.CacheElement;
import com.outbrain.pajamasproxy.memcached.server.protocol.exceptions.UnknownCommandException;
import com.outbrain.pajamasproxy.memcached.server.protocol.value.Op;
import com.outbrain.pajamasproxy.memcached.server.protocol.value.ResponseMessage;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.springframework.util.Assert;

/**
 *
 */
// TODO refactor so this can be unit tested separate from netty? scalacheck?
@ChannelHandler.Sharable
public class MemcachedBinaryResponseEncoder extends MessageToByteEncoder<ResponseMessage> {

  final Logger logger = LoggerFactory.getLogger(MemcachedBinaryResponseEncoder.class);
  private final ConcurrentHashMap<Integer, ByteBuf> corkedBuffers = new ConcurrentHashMap<Integer, ByteBuf>();

  public ResponseCode getStatusCode(final ResponseMessage command) {
    final Op cmd = command.cmd.op;
    if (cmd == Op.GET || cmd == Op.GETS) {
      return command.elements[0] == null ? ResponseCode.KEYNF : ResponseCode.OK;
    } else if (cmd == Op.SET || cmd == Op.CAS || cmd == Op.ADD || cmd == Op.REPLACE || cmd == Op.APPEND || cmd == Op.PREPEND) {
      switch (command.response) {
      case EXISTS:
        return ResponseCode.KEYEXISTS;
      case NOT_FOUND:
        return ResponseCode.KEYNF;
      case NOT_STORED:
        return ResponseCode.NOT_STORED;
      case STORED:
        return ResponseCode.OK;
      }
    } else if (cmd == Op.INCR || cmd == Op.DECR) {
      return command.incrDecrResponse == null ? ResponseCode.KEYNF : ResponseCode.OK;
    } else if (cmd == Op.DELETE) {
      switch (command.deleteResponse) {
      case DELETED:
        return ResponseCode.OK;
      case NOT_FOUND:
        return ResponseCode.KEYNF;
      }
    } else if (cmd == Op.STATS) {
      return ResponseCode.OK;
    } else if (cmd == Op.VERSION) {
      return ResponseCode.OK;
    } else if (cmd == Op.FLUSH_ALL) {
      return ResponseCode.OK;
    }
    return ResponseCode.UNKNOWN;
  }

  public ByteBuf constructHeader(final MemcachedBinaryCommandDecoder.BinaryOp bcmd, final ByteBuf extrasBuffer, final ByteBuf keyBuffer,
      final ByteBuf valueBuffer, final short responseCode, final int opaqueValue, final long casUnique) {
    // take the ResponseMessage and turn it into a binary payload.
    final ByteBuf header = Unpooled.buffer(24);
    header.writeByte((byte) 0x81); // magic
    header.writeByte(bcmd.code); // opcode
    final short keyLength = (short) (keyBuffer != null ? keyBuffer.capacity() : 0);

    header.writeShort(keyLength);
    final int extrasLength = extrasBuffer != null ? extrasBuffer.capacity() : 0;
    header.writeByte((byte) extrasLength); // extra length = flags + expiry
    header.writeByte((byte) 0); // data type unused
    header.writeShort(responseCode); // status code

    final int dataLength = valueBuffer != null ? valueBuffer.capacity() : 0;
    header.writeInt(dataLength + keyLength + extrasLength); // data length
    header.writeInt(opaqueValue); // opaque

    header.writeLong(casUnique);

    return header;
  }

  /**
   * Handle exceptions in protocol processing. Exceptions are either client or internal errors.  Report accordingly.
   *
   * @param ctx
   * @param e
   * @throws Exception
   */
  @Override
  public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable e) throws Exception {
    try {
      throw e;
    } catch (final UnknownCommandException unknownCommand) {
      logger.error("Unknown command...", unknownCommand);
      if (ctx.channel().isOpen()) {
        ctx.channel().write(constructHeader(MemcachedBinaryCommandDecoder.BinaryOp.Noop, null, null, null, (short) 0x0081, 0, 0));
      }
    } catch (final Throwable err) {
      logger.error("error", err);
      if (ctx.channel().isOpen()) {
        ctx.channel().close();
      }
    }
  }

  @Override
  protected void encode(ChannelHandlerContext channelHandlerContext, ResponseMessage command, ByteBuf out) throws Exception {

    final MemcachedBinaryCommandDecoder.BinaryOp bcmd = MemcachedBinaryCommandDecoder.BinaryOp.forCommandMessage(command.cmd);
    logger.debug("encoding {}", bcmd);
    // write extras == flags & expiry
    ByteBuf extrasBuffer = null;

    // write key if there is one
    ByteBuf keyBuffer = null;
    if (bcmd.addKeyToResponse && command.cmd.key != null) {
      keyBuffer = command.cmd.key.bytes;
    }

    // write value if there is one
    ByteBuf valueBuffer = null;
    if (command.elements != null) {
      final CacheElement element = command.elements[0];
      if (element != null) {
        extrasBuffer = Unpooled.buffer(4);
        extrasBuffer.writeShort((short) element.getExpire());
        extrasBuffer.writeShort((short) element.getFlags());
      }

      if ((command.cmd.op == Op.GET || command.cmd.op == Op.GETS)) {
        if (element != null) {
          valueBuffer = Unpooled.wrappedBuffer(element.getData());
        } else {
          valueBuffer = Unpooled.EMPTY_BUFFER;
          extrasBuffer = null;
        }
      } else if (command.cmd.op == Op.INCR || command.cmd.op == Op.DECR) {
        valueBuffer = Unpooled.buffer(8);
        valueBuffer.writeLong(command.incrDecrResponse);
      }
    } else if (command.cmd.op == Op.INCR || command.cmd.op == Op.DECR) {
      valueBuffer = Unpooled.buffer(8);
      valueBuffer.writeLong(command.incrDecrResponse);
    }

    long casUnique = 0;
    if (command.elements != null && command.elements.length != 0 && command.elements[0] != null) {
      casUnique = command.elements[0].getCasUnique();
    }

    // stats is special -- with it, we write N times, one for each stat, then an empty payload
    if (command.cmd.op == Op.STATS) {
      // first uncork any corked buffers
      if (corkedBuffers.containsKey(command.cmd.opaque)) {
        uncork(command.cmd.opaque, out);
      }

      for (final Map.Entry<String, Set<String>> statsEntries : command.stats.entrySet()) {
        for (final String stat : statsEntries.getValue()) {

          keyBuffer = Unpooled.wrappedBuffer(statsEntries.getKey().getBytes(MemcachedBinaryCommandDecoder.USASCII));
          valueBuffer = Unpooled.wrappedBuffer(stat.getBytes(MemcachedBinaryCommandDecoder.USASCII));

          final ByteBuf headerBuffer = constructHeader(bcmd, extrasBuffer, keyBuffer, valueBuffer, getStatusCode(command).code, command.cmd.opaque,
              casUnique);

          writePayload(out, extrasBuffer, keyBuffer, valueBuffer, headerBuffer);
        }
      }

      keyBuffer = null;
      valueBuffer = null;

      final ByteBuf headerBuffer = constructHeader(bcmd, extrasBuffer, keyBuffer, valueBuffer, getStatusCode(command).code, command.cmd.opaque,
          casUnique);

      writePayload(out, extrasBuffer, keyBuffer, valueBuffer, headerBuffer);

    } else {
      final ByteBuf headerBuffer = constructHeader(bcmd, extrasBuffer, keyBuffer, valueBuffer, getStatusCode(command).code, command.cmd.opaque,
          casUnique);

      // write everything
      // is the command 'quiet?' if so, then we append to our 'corked' buffer until a non-corked command comes along
      if (bcmd.noreply) {
        final int totalCapacity = headerBuffer.capacity() + (extrasBuffer != null ? extrasBuffer.capacity() : 0)
            + (keyBuffer != null ? keyBuffer.capacity() : 0) + (valueBuffer != null ? valueBuffer.capacity() : 0);

        final ByteBuf corkedResponse = cork(command.cmd.opaque, totalCapacity);

        corkedResponse.writeBytes(headerBuffer);
        if (extrasBuffer != null) {
          corkedResponse.writeBytes(extrasBuffer);
        }
        if (keyBuffer != null) {
          corkedResponse.writeBytes(keyBuffer);
        }
        if (valueBuffer != null) {
          corkedResponse.writeBytes(valueBuffer);
        }
      } else {
        // first write out any corked responses
        if (corkedBuffers.containsKey(command.cmd.opaque)) {
          uncork(command.cmd.opaque, out);
        }

        writePayload(out, extrasBuffer, keyBuffer, valueBuffer, headerBuffer);
      }
    }
  }

  private ByteBuf cork(final int opaque, final int totalCapacity) {
    ByteBuf corkedResponse = corkedBuffers.get(opaque);
    if (corkedResponse == null) {
      corkedResponse = Unpooled.buffer(totalCapacity);
      corkedBuffers.put(opaque, corkedResponse);
    }

    return corkedResponse;
  }

  private void uncork(final int opaque, final ByteBuf out) {
    final ByteBuf corkedBuffer = corkedBuffers.remove(opaque);
    Assert.state(corkedBuffer != null, "corked buffer is null, but not expected to be");
    out.writeBytes(corkedBuffer);
  }

  private void writePayload(final ByteBuf out, final ByteBuf extrasBuffer, final ByteBuf keyBuffer, final ByteBuf valueBuffer,
      final ByteBuf headerBuffer) {
    out.writeBytes(headerBuffer);
    if (extrasBuffer != null) {
      out.writeBytes(extrasBuffer);
    }
    if (keyBuffer != null) {
      out.writeBytes(keyBuffer);
    }
    if (valueBuffer != null) {
      out.writeBytes(valueBuffer);
    }
  }

  public static enum ResponseCode {
    OK(0x0000),
    KEYNF(0x0001),
    KEYEXISTS(0x0002),
    TOOLARGE(0x0003),
    INVARG(0x0004),
    NOT_STORED(0x0005),
    UNKNOWN(0x0081),
    OOM(0x00082);
    public short code;

    ResponseCode(final int code) {
      this.code = (short) code;
    }
  }
}
