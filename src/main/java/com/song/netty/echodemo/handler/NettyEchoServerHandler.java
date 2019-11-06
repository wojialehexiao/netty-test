package com.song.netty.echodemo.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

import java.nio.charset.StandardCharsets;

public class NettyEchoServerHandler extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        System.out.println("NettyEchoServerHandler");
        ByteBuf buf = (ByteBuf) msg;

        int len = buf.readableBytes();

        byte[] array = new byte[len];
        buf.getBytes(0, array);

        System.out.println("server receive: " + new String(array, StandardCharsets.UTF_8));

        ctx.writeAndFlush(array);

        super.write(ctx, msg, promise);
    }
}
