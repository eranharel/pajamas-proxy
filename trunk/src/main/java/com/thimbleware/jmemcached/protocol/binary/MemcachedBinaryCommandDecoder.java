package com.thimbleware.jmemcached.protocol.binary;

import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import com.thimbleware.jmemcached.CacheElement;
import com.thimbleware.jmemcached.Key;
import com.thimbleware.jmemcached.LocalCacheElement;
import com.thimbleware.jmemcached.protocol.CommandMessage;
import com.thimbleware.jmemcached.protocol.Op;
import com.thimbleware.jmemcached.protocol.exceptions.MalformedCommandException;

/**
 */
@ChannelHandler.Sharable
public class MemcachedBinaryCommandDecoder extends FrameDecoder {

  public static final Charset USASCII = Charset.forName("US-ASCII");

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
      this.code = (byte)code;
      this.correspondingOp = correspondingOp;
      this.noreply = noreply;
    }

    BinaryOp(final int code, final Op correspondingOp, final boolean noreply, final boolean addKeyToResponse) {
      this.code = (byte)code;
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

  @Override
  protected Object decode(final ChannelHandlerContext channelHandlerContext, final Channel channel, final ChannelBuffer channelBuffer) throws Exception {

    // need at least 24 bytes, to get header
    if (channelBuffer.readableBytes() < 24) {
      return null;
    }

    // get the header
    channelBuffer.markReaderIndex();
    final ChannelBuffer headerBuffer = ChannelBuffers.buffer(ByteOrder.BIG_ENDIAN, 24);
    channelBuffer.readBytes(headerBuffer);

    final short magic = headerBuffer.readUnsignedByte();

    // magic should be 0x80
    if (magic != 0x80) {
      headerBuffer.resetReaderIndex();

      throw new MalformedCommandException("binary request payload is invalid, magic byte incorrect");
    }

    final short opcode = headerBuffer.readUnsignedByte();
    final short keyLength = headerBuffer.readShort();
    final short extraLength = headerBuffer.readUnsignedByte();
    /*final short dataType = */headerBuffer.readUnsignedByte(); // unused
    /*final short reserved = */headerBuffer.readShort(); // unused
    final int totalBodyLength = headerBuffer.readInt();
    final int opaque = headerBuffer.readInt();
    final long cas = headerBuffer.readLong();

    // we want the whole of totalBodyLength; otherwise, keep waiting.
    if (channelBuffer.readableBytes() < totalBodyLength) {
      channelBuffer.resetReaderIndex();
      return null;
    }


    // This assumes correct order in the enum. If that ever changes, we will have to scan for 'code' field.
    final BinaryOp bcmd = BinaryOp.values()[opcode];

    final Op cmdType = bcmd.correspondingOp;

    final CommandMessage cmdMessage = CommandMessage.command(cmdType);
    cmdMessage.noreply = bcmd.noreply;
    cmdMessage.cas_key = cas;
    cmdMessage.opaque = opaque;
    cmdMessage.addKeyToResponse = bcmd.addKeyToResponse;

    // get extras. could be empty.
    final ChannelBuffer extrasBuffer = ChannelBuffers.buffer(ByteOrder.BIG_ENDIAN, extraLength);
    channelBuffer.readBytes(extrasBuffer);

    // get the key if any
    if (keyLength != 0) {
      final ChannelBuffer keyBuffer = ChannelBuffers.buffer(ByteOrder.BIG_ENDIAN, keyLength);
      channelBuffer.readBytes(keyBuffer);

      final ArrayList<Key> keys = new ArrayList<Key>();
      keys.add(new Key(keyBuffer.copy()));

      cmdMessage.keys = keys;


      if (cmdType == Op.ADD ||
          cmdType == Op.SET ||
          cmdType == Op.REPLACE ||
          cmdType == Op.APPEND ||
          cmdType == Op.PREPEND)
      {
        // TODO these are backwards from the spec, but seem to be what spymemcached demands -- which has the mistake?!
        final short expire = (short) (extrasBuffer.capacity() != 0 ? extrasBuffer.readUnsignedShort() : 0);
        final short flags = (short) (extrasBuffer.capacity() != 0 ? extrasBuffer.readUnsignedShort() : 0);

        // the remainder of the message -- that is, totalLength - (keyLength + extraLength) should be the payload
        final int size = totalBodyLength - keyLength - extraLength;

        cmdMessage.element = new LocalCacheElement(new Key(keyBuffer.slice()), flags, expire != 0 && expire < CacheElement.THIRTY_DAYS ? LocalCacheElement.Now() + expire : expire, 0L);
        final ChannelBuffer data = ChannelBuffers.buffer(size);
        channelBuffer.readBytes(data);
        cmdMessage.element.setData(data);
      } else if (cmdType == Op.INCR || cmdType == Op.DECR) {
        /*final long initialValue = */extrasBuffer.readUnsignedInt(); // unused
        final long amount = extrasBuffer.readUnsignedInt();
        final long expiration = extrasBuffer.readUnsignedInt();

        cmdMessage.incrAmount = (int) amount;
        cmdMessage.incrExpiry = (int) expiration;
      }
    }

    return cmdMessage;
  }
}
