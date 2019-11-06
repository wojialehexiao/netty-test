package com.song.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class InPipeline {

    public static class SimpleInHandlerA extends ChannelInboundHandlerAdapter{

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("入站处理器A： 被调用");
            ctx.pipeline().remove(this);
            super.channelRead(ctx, msg);
        }
    }
    public static class SimpleInHandlerB extends ChannelInboundHandlerAdapter{

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("入站处理器B： 被调用");
//            super.channelRead(ctx, msg);
            ctx.fireChannelRead(msg);
        }
    }

    static class SimpleInHandlerC extends ChannelInboundHandlerAdapter{

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("入站处理器C： 被调用");
            super.channelRead(ctx, msg);
        }
    }


}
