package com.sky.framework.rpc.remoting.protocol;

import com.sky.framework.rpc.remoting.Request;
import com.sky.framework.rpc.remoting.Response;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author
 */
public class ProtocolEncoder extends MessageToByteEncoder<PayloadHolder> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, PayloadHolder msg, ByteBuf out) throws Exception {
        if (msg instanceof Request) {
            doEncodeRequest((Request) msg, out);
        } else if (msg instanceof Response) {
            doEncodeResponse((Response) msg, out);
        } else {
            throw new IllegalArgumentException();
        }
    }

    private void doEncodeRequest(Request request, ByteBuf out) {
        byte sign = ProtocolHeader.toSign(request.serializerCode(), ProtocolHeader.REQUEST);
        long invokeId = request.getId();
        byte[] bytes = request.bytes();
        int length = bytes.length;

        out.writeShort(ProtocolHeader.MAGIC)
                .writeByte(sign)
                .writeByte(0x00)
                .writeLong(invokeId)
                .writeInt(length)
                .writeBytes(bytes);
    }


    private void doEncodeResponse(Response response, ByteBuf out) {
        byte sign = ProtocolHeader.toSign(response.serializerCode(), ProtocolHeader.RESPONSE);
        byte status = response.status();
        long invokeId = response.id();
        byte[] bytes = response.bytes();
        int length = bytes.length;

        out.writeShort(ProtocolHeader.MAGIC)
                .writeByte(sign)
                .writeByte(status)
                .writeLong(invokeId)
                .writeInt(length)
                .writeBytes(bytes);
    }
}
