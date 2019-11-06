package com.song.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class IntegerProcessHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Integer m = (Integer) msg;
        System.out.println("打印出一个整数" + m);
        super.channelRead(ctx, msg);
    }
}
