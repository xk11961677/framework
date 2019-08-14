package com.sky.framework.rpc.remoting.codec;

import com.sky.framework.rpc.util.NumberUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.Charset;
import java.util.List;

/**
 * simple codec protocol
 *
 * @author
 */
public class RpcDecoder extends ByteToMessageDecoder {

    private final static Charset charset = Charset.defaultCharset();

    public RpcDecoder() {

    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        if (msg.readableBytes() > 4) {
            msg.markReaderIndex();
            byte[] b = new byte[4];
            msg.readBytes(b);
            int l = NumberUtils.byteArrayToInt(b);
            if (msg.readableBytes() < l) {
                msg.resetReaderIndex();
                return;
            }
            msg.markReaderIndex();
            byte[] decoded = new byte[l];
            msg.readBytes(decoded);
            out.add(new String(decoded, charset));
        }
    }
}
