package com.song.netty.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class StringReplayDecoder extends ReplayingDecoder<StringReplayDecoder.Status> {

    private int length;

    private byte[] inBytes;

    public StringReplayDecoder() {
        super(Status.PARSE_1);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        switch (state()) {
            case PARSE_1:
                length = in.readInt();
                inBytes = new byte[length];
                checkpoint(Status.PARSE_2);
                break;
            case PARSE_2:

                in.readBytes(inBytes, 0, length);
                out.add(new String(inBytes, StandardCharsets.UTF_8));
                checkpoint(Status.PARSE_1);
                break;
            default:
                break;
        }
    }

    enum Status {
        PARSE_1, PARSE_2
    }
}
