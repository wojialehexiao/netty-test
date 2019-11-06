package com.song.netty.handler;

import com.song.netty.decoder.Byte2IntegerDecoder;
import com.song.netty.encoder.Integer2ByteEncoder;
import io.netty.channel.CombinedChannelDuplexHandler;

public class IntegerDuplexHandler extends CombinedChannelDuplexHandler<Byte2IntegerDecoder, Integer2ByteEncoder> {

    public IntegerDuplexHandler(Byte2IntegerDecoder inboundHandler, Integer2ByteEncoder outboundHandler) {
        super(inboundHandler, outboundHandler);
    }
}
