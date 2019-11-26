/*
 * The MIT License (MIT)
 * Copyright © 2019 <sky>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the “Software”), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sky.framework.rpc.remoting.protocol;

import com.sky.framework.rpc.remoting.Request;
import com.sky.framework.rpc.remoting.Response;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author
 */
@Slf4j
public class ProtocolDecoder extends ReplayingDecoder<ProtocolDecoder.State> {

    /**
     * 消息内容最大长度
     */
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
        switch (state()) {
            case MAGIC:
                // MAGIC
                checkMagic(in.readShort());
                checkpoint(State.SIGN);
            case SIGN:
                // 消息标志位
                header.sign(in.readByte());
                checkpoint(State.STATUS);
            case STATUS:
                // 状态位
                header.setStatus(in.readByte());
                checkpoint(State.ID);
            case ID:
                // 消息id
                header.setId(in.readLong());
                checkpoint(State.BODY_SIZE);
            case BODY_SIZE:
                // 消息体长度
                header.setBodySize(in.readInt());
                checkpoint(State.BODY);
            case BODY:
                // 消息体内容
                switch (header.getMessageCode()) {
                    case ProtocolHeader.HEARTBEAT:
                        break;
                    case ProtocolHeader.REQUEST: {
                        int length = checkBodySize(header.getBodySize());
                        byte[] bytes = new byte[length];
                        in.readBytes(bytes);
                        Request request = new Request(header.getId());
                        request.timestamp(System.currentTimeMillis());
                        request.bytes(header.getSerializerCode(), bytes);
                        out.add(request);
                        break;
                    }
                    case ProtocolHeader.RESPONSE: {
                        int length = checkBodySize(header.getBodySize());
                        byte[] bytes = new byte[length];
                        in.readBytes(bytes);
                        Response response = new Response(header.getId());
                        response.status(header.getStatus());
                        response.bytes(header.getSerializerCode(), bytes);
                        out.add(response);
                        break;
                    }
                    default:
                        log.info("the protocol decoder not supported!");
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
