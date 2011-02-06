package com.outbrain.pajamasproxy.memcached.protocol.binary;

import java.nio.charset.Charset;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import com.outbrain.pajamasproxy.memcached.protocol.MemcachedCommand;

public class BinaryCommandDecoder extends FrameDecoder {

  @Override
  protected Object decode(final ChannelHandlerContext ctx, final Channel channel, final ChannelBuffer buffer) throws Exception {
    /*
     * Based on http://code.google.com/p/memcached/wiki/MemcacheBinaryProtocol

      Request header:

     Byte/     0       |       1       |       2       |       3       |
        /              |               |               |               |
       |0 1 2 3 4 5 6 7|0 1 2 3 4 5 6 7|0 1 2 3 4 5 6 7|0 1 2 3 4 5 6 7|
       +---------------+---------------+---------------+---------------+
      0| Magic         | Opcode        | Key length                    |
       +---------------+---------------+---------------+---------------+
      4| Extras length | Data type     | Reserved                      |
       +---------------+---------------+---------------+---------------+
      8| Total body length                                             |
       +---------------+---------------+---------------+---------------+
     12| Opaque                                                        |
       +---------------+---------------+---------------+---------------+
     16| CAS                                                           |
       |                                                               |
       +---------------+---------------+---------------+---------------+
       Total 24 bytes

       We only need the key length, and Total body length to be able to parse the message frame size, and the key.
     */
    if (buffer.readableBytes() < 12) {
      return null;
    }

    buffer.markReaderIndex();

    final ChannelBuffer partialHeaderBuffer = ChannelBuffers.buffer(12);
    buffer.readBytes(partialHeaderBuffer);

    partialHeaderBuffer.readUnsignedByte(); // Magic
    final short opcode = partialHeaderBuffer.readUnsignedByte();
    final short keyLength = partialHeaderBuffer.readShort();
    final short extrasLength = partialHeaderBuffer.readUnsignedByte();
    partialHeaderBuffer.readUnsignedByte(); // Data type
    partialHeaderBuffer.readShort(); // Reserved
    final int totalBodyLength = partialHeaderBuffer.readInt();

    // now we want the whole of the message; otherwise, keep waiting.
    if (buffer.readableBytes() < 12 + totalBodyLength) {
      buffer.resetReaderIndex();
      return null;
    }

    // read extras... ignore for now
    buffer.readBytes(ChannelBuffers.buffer(extrasLength));

    String key = null;
    if (0 < keyLength) {
      buffer.readBytes(ChannelBuffers.buffer(12)); // we want the key now
      final ChannelBuffer keyBuffer = ChannelBuffers.buffer(keyLength);
      buffer.readBytes(keyBuffer);
      key = keyBuffer.toString(Charset.forName("utf-8"));
    }

    // read value if any
    final int dataSize = totalBodyLength - keyLength - extrasLength;
    final ChannelBuffer dataBuffer = ChannelBuffers.buffer(dataSize);
    buffer.readBytes(dataBuffer);

    return new MemcachedCommand(key, dataBuffer.array(), (byte) opcode);
  }

}
