package com.song.netty.echodemo.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;

public class NettyEchoClientHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        ByteBuf buf = (ByteBuf) msg;

        int len = buf.readableBytes();
        byte[] arr = new byte[len];

        buf.getBytes(0, arr);

        System.out.println("client recevived: " + new String(arr, StandardCharsets.UTF_8));

        buf.release();

//        super.channelRead(ctx, msg);
    }
}
