package com.outbrain.pajamasproxy.memcached.server.protocol.binary;

import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.outbrain.pajamasproxy.memcached.adapter.Key;
import com.outbrain.pajamasproxy.memcached.adapter.LocalCacheElement;
import com.outbrain.pajamasproxy.memcached.server.protocol.exceptions.MalformedCommandException;
import com.outbrain.pajamasproxy.memcached.server.protocol.value.CommandMessage;
import com.outbrain.pajamasproxy.memcached.server.protocol.value.Op;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

//@ChannelHandler.Sharable
public class MemcachedBinaryCommandDecoder extends ByteToMessageDecoder implements DecodingStatistics {

  public static final Charset USASCII = Charset.forName("US-ASCII");
  private static final AtomicLong decodingErrors = new AtomicLong();
  private final Logger logger = LoggerFactory.getLogger(MemcachedBinaryCommandDecoder.class);

  @Override
  protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {

    // need at least 24 bytes, to get header
    if (in.readableBytes() < 24) {
      return;
    }

    // get the header
    in.markReaderIndex();

    final short magic = in.readUnsignedByte();

    // magic should be 0x80
    if (magic != 0x80) {
      in.resetReaderIndex();
      throw new MalformedCommandException("binary request payload is invalid, magic byte incorrect");
    }

    final short opcode = in.readUnsignedByte();
    final short keyLength = in.readShort();
    final short extraLength = in.readUnsignedByte();
    /*final short dataType = */in.readUnsignedByte(); // unused
    /*final short reserved = */in.readShort(); // unused
    final int totalBodyLength = in.readInt();
    final int opaque = in.readInt();
    final long cas = in.readLong();

    // we want the whole of totalBodyLength; otherwise, keep waiting.
    if (in.readableBytes() < totalBodyLength) {
      in.resetReaderIndex();
      return;
    }

    // This assumes correct order in the enum. If that ever changes, we will have to scan for 'code' field.
    final BinaryOp bcmd = BinaryOp.values()[opcode];
    logger.debug("Decoding *{}* command", bcmd);

    final Op cmdType = bcmd.correspondingOp;

    final CommandMessage cmdMessage = CommandMessage.command(cmdType);
    cmdMessage.noreply = bcmd.noreply;
    cmdMessage.cas_key = cas;
    cmdMessage.opaque = opaque;
    cmdMessage.addKeyToResponse = bcmd.addKeyToResponse;

    // get extras. could be empty.
    final ByteBuf extrasBuffer = Unpooled.buffer(extraLength);
    in.readBytes(extrasBuffer);

    // get the key if any
    if (keyLength != 0) {
      final ByteBuf keyBuffer = Unpooled.buffer(keyLength);
      in.readBytes(keyBuffer);
      cmdMessage.key = new Key(keyBuffer);

      if (cmdType == Op.ADD || cmdType == Op.SET || cmdType == Op.REPLACE || cmdType == Op.APPEND || cmdType == Op.PREPEND) {
        // TODO these are backwards from the spec, but seem to be what spymemcached demands -- which has the mistake?!
        final int flags = (int) (extrasBuffer.capacity() != 0 ? extrasBuffer.readUnsignedInt() : 0);
        final int expire = (int) (extrasBuffer.capacity() != 0 ? extrasBuffer.readUnsignedInt() : 0);

        // the remainder of the message -- that is, totalLength - (keyLength + extraLength) should be the payload
        final int size = totalBodyLength - keyLength - extraLength;

        cmdMessage.element = new LocalCacheElement(cmdMessage.key, flags, expire, 0L);
        final ByteBuf data = Unpooled.buffer(size);
        in.readBytes(data);
        cmdMessage.element.setData(data);
      } else if (cmdType == Op.INCR || cmdType == Op.DECR) {
        /*final long initialValue = */extrasBuffer.readUnsignedInt(); // unused
        final long amount = extrasBuffer.readUnsignedInt();
        final long expiration = extrasBuffer.readUnsignedInt();

        cmdMessage.incrAmount = (int) amount;
        cmdMessage.incrExpiry = (int) expiration;
      }
    }

    out.add(cmdMessage);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    decodingErrors.incrementAndGet();
    logger.warn("{};  remoteAddress={}; closing channel...", cause, ctx.channel().remoteAddress());
    ctx.channel().close();
  }

  /* (non-Javadoc)
     * @see com.outbrain.pajamasproxy.memcached.server.protocol.binary.DecodingStatistics#getDecodingErrors()
     */
  @Override
  public long getDecodingErrors() {
    return decodingErrors.get();
  }

  public static enum BinaryOp {
    Get(0x00, Op.GET, false),
    Set(0x01, Op.SET, false),
    Add(0x02, Op.ADD, false),
    Replace(0x03, Op.REPLACE, false),
    Delete(0x04, Op.DELETE, false),
    Increment(0x05, Op.INCR, false),
    Decrement(0x06, Op.DECR, false),
    Quit(0x07, Op.QUIT, false),
    Flush(0x08, Op.FLUSH_ALL, false),
    GetQ(0x09, Op.GET, false),
    Noop(0x0A, null, false),
    Version(0x0B, Op.VERSION, false),
    GetK(0x0C, Op.GET, false, true),
    GetKQ(0x0D, Op.GET, true, true),
    Append(0x0E, Op.APPEND, false),
    Prepend(0x0F, Op.PREPEND, false),
    Stat(0x10, Op.STATS, false),
    SetQ(0x11, Op.SET, true),
    AddQ(0x12, Op.ADD, true),
    ReplaceQ(0x13, Op.REPLACE, true),
    DeleteQ(0x14, Op.DELETE, true),
    IncrementQ(0x15, Op.INCR, true),
    DecrementQ(0x16, Op.DECR, true),
    QuitQ(0x17, Op.QUIT, true),
    FlushQ(0x18, Op.FLUSH_ALL, true),
    AppendQ(0x19, Op.APPEND, true),
    PrependQ(0x1A, Op.PREPEND, true);
    public byte code;
    public Op correspondingOp;
    public boolean noreply;
    public boolean addKeyToResponse = false;

    BinaryOp(final int code, final Op correspondingOp, final boolean noreply) {
      this.code = (byte) code;
      this.correspondingOp = correspondingOp;
      this.noreply = noreply;
    }

    BinaryOp(final int code, final Op correspondingOp, final boolean noreply, final boolean addKeyToResponse) {
      this.code = (byte) code;
      this.correspondingOp = correspondingOp;
      this.noreply = noreply;
      this.addKeyToResponse = addKeyToResponse;
    }

    public static BinaryOp forCommandMessage(final CommandMessage msg) {
      for (final BinaryOp binaryOp : values()) {
        if (binaryOp.correspondingOp == msg.op && binaryOp.noreply == msg.noreply && binaryOp.addKeyToResponse == msg.addKeyToResponse) {
          return binaryOp;
        }
      }

      return null;
    }

  }
}
