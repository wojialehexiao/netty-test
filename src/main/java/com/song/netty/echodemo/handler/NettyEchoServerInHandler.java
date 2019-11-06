package com.song.netty.echodemo.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;

public class NettyEchoServerInHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        ByteBuf buf = (ByteBuf) msg;
        System.out.println("refCnt" + buf.refCnt());

        int len = buf.readableBytes();

        byte[] array = new byte[len];
        buf.getBytes(0, array);

        System.out.println("server receive: " + new String(array, StandardCharsets.UTF_8));
        System.out.println("refCnt" + buf.refCnt());

        buf.retain();
        ctx.writeAndFlush(msg);

        System.out.println("refCnt" + buf.refCnt());


        super.channelRead(ctx, msg);
    }
}
