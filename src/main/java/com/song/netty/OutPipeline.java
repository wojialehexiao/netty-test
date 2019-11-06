package com.song.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class OutPipeline {

    public static class SimpleOutHandlerA extends ChannelOutboundHandlerAdapter {

        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            System.out.println("出站处理器A： 被调用");
            super.write(ctx, msg, promise);
        }
    }
    static class SimpleOutHandlerB extends ChannelOutboundHandlerAdapter {

        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            System.out.println("出站处理器B： 被调用");
            super.write(ctx, msg, promise);
        }
    }
    static class SimpleOutHandlerC extends ChannelOutboundHandlerAdapter {

        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            System.out.println("出站处理器C： 被调用");
            super.write(ctx, msg, promise);
        }
    }


}
