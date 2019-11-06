package com.song.netty.encoder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class String2IntegerEncoder extends MessageToMessageEncoder<String> {

    @Override
    protected void encode(ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {
        char[] chars = msg.toCharArray();
        for (char a : chars) {
            if(a >= 48 && a<= 57){
                out.add((int) a);
            }
        }
    }
}
