package com.song.netty.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

public class IntegerAddDecoder extends ReplayingDecoder<IntegerAddDecoder.Status> {

    private int first;
    private int second;

    public IntegerAddDecoder() {
        super(Status.PARSE_1);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        switch (state()) {
            case PARSE_1:
                first = in.readInt();
                checkpoint(Status.PARSE_2);
                break;
            case PARSE_2:
                second = in.readInt();
                Integer sum = first + second;
                out.add(sum);
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
