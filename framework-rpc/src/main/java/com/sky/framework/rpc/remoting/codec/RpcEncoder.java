package com.sky.framework.rpc.remoting.codec;

import com.sky.framework.rpc.util.NumberUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.Charset;

/**
 * simple codec protocol
 *
 * @author
 */
public class RpcEncoder extends MessageToByteEncoder<String> {


    private final static Charset charset = Charset.defaultCharset();

    public RpcEncoder() {
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, String s, ByteBuf out) throws Exception {
        int length = s.length();

        byte[] bytes = NumberUtils.intToByteArray(length);

        int startIdx = out.writerIndex();

        ByteBufOutputStream bout = new ByteBufOutputStream(out);
        bout.write(bytes);
        bout.write(s.getBytes(charset));
        bout.flush();
        bout.close();

        int endIdx = out.writerIndex();

        out.setInt(startIdx, endIdx - startIdx - 4);

    }
}
