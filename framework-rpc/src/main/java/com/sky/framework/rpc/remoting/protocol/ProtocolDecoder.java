package com.sky.framework.rpc.remoting.protocol;

import com.sky.framework.rpc.remoting.Request;
import com.sky.framework.rpc.remoting.Response;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * @author
 */
public class ProtocolDecoder extends ReplayingDecoder<ProtocolDecoder.State> {

    private static final int MAX_BODY_SIZE = 1024 * 1024 * 5;


    public ProtocolDecoder() {
        super(State.MAGIC);
    }

    /**
     * 协议头
     */
    private final ProtocolHeader header = new ProtocolHeader();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println("decoder");
        switch (state()) {
            case MAGIC:
                checkMagic(in.readShort());         // MAGIC
                checkpoint(State.SIGN);
            case SIGN:
                header.sign(in.readByte());         // 消息标志位
                checkpoint(State.STATUS);
            case STATUS:
                header.status(in.readByte());       // 状态位
                checkpoint(State.ID);
            case ID:
                header.id(in.readLong());           // 消息id
                checkpoint(State.BODY_SIZE);
            case BODY_SIZE:
                header.bodySize(in.readInt());      // 消息体长度
                checkpoint(State.BODY);
            case BODY:
                switch (header.messageCode()) {
                    case ProtocolHeader.HEARTBEAT:
                        break;
                    case ProtocolHeader.REQUEST: {
                        int length = checkBodySize(header.bodySize());
                        byte[] bytes = new byte[length];
                        in.readBytes(bytes);
                        Request request = new Request(header.id());
                        request.timestamp(System.currentTimeMillis());
                        request.bytes(header.serializerCode(), bytes);

                        out.add(request);

                        break;
                    }
                    case ProtocolHeader.RESPONSE: {
                        int length = checkBodySize(header.bodySize());
                        byte[] bytes = new byte[length];
                        in.readBytes(bytes);

                        Response response = new Response(header.id());
                        response.status(header.status());
                        response.bytes(header.serializerCode(), bytes);

                        out.add(response);

                        break;
                    }
                    default:
                        System.out.println("not supported");
                }
                checkpoint(State.MAGIC);
        }
    }

    private static void checkMagic(short magic) throws RuntimeException {
        if (magic != ProtocolHeader.MAGIC) {
            throw new RuntimeException("ILLEGAL_MAGIC");
        }
    }

    private static int checkBodySize(int size) throws RuntimeException {
        if (size > MAX_BODY_SIZE) {
            throw new RuntimeException("BODY_TOO_LARGE");
        }
        return size;
    }

    enum State {
        MAGIC,
        SIGN,
        STATUS,
        ID,
        BODY_SIZE,
        BODY
    }
}
