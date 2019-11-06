package com.song.netty.echodemo;

import com.song.netty.echodemo.handler.NettyEchoClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.nio.charset.StandardCharsets;

public class NettyEchoClient {

    private int serverPort;

    private String serverIp;

    String content = "详解粘包和拆包";

    Bootstrap b = new Bootstrap();

    public NettyEchoClient(String ip, int port) {
        this.serverIp = ip;
        this.serverPort = port;
    }

    public void runClient() {

        EventLoopGroup workerLoopGroup = new NioEventLoopGroup();

        try {
            b.group(workerLoopGroup);

            b.channel(NioSocketChannel.class);

            b.remoteAddress(serverIp, serverPort);

            b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new NettyEchoClientHandler());
                }
            });

            ChannelFuture connect = b.connect();
            connect.addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    if(future.isSuccess()){
                        System.out.println("EchoClient客户端连接成功！");
                    }else {
                        System.out.println("EchoClient客户端连接失败！");
                    }
                }
            });

            connect.sync();

            Channel channel = connect.channel();

//            Scanner scanner = new Scanner(System.in);
//            System.out.println("请输入内容：");
//            while (scanner.hasNext()){
//                String next = scanner.next();
//                byte[] bytes = (DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss") + " >> " + next).getBytes(StandardCharsets.UTF_8);
//                ByteBuf buffer = channel.alloc().buffer();
//                buffer.writeBytes(bytes);
//                channel.writeAndFlush(buffer);
//                System.out.println("请输入内容：");
//            }

            for (int i = 0; i < 1000; i++) {
                byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
                ByteBuf buffer = channel.alloc().buffer();
                buffer.writeBytes(bytes);
                channel.writeAndFlush(buffer);
            }


        }catch (Exception e){
            e.printStackTrace();
        }finally {
            workerLoopGroup.shutdownGracefully();
        }

    }

    public static void main(String[] args) {
        new NettyEchoClient("127.0.0.1", 8000).runClient();
    }
}
