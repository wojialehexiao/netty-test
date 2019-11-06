package com.song.netty.echodemo;

import com.song.netty.NettyDemoConfig;
import com.song.netty.echodemo.handler.NettyEchoServerInHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyEchoServer {

    private final int serverPort;

    ServerBootstrap b = new ServerBootstrap();

    public NettyEchoServer(int port) {
        this.serverPort = port;
    }

    public void runServer() {
        EventLoopGroup bossLoopGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerLoopGroup = new NioEventLoopGroup();
        try {

            b.group(bossLoopGroup, workerLoopGroup);

            b.channel(NioServerSocketChannel.class);

            b.localAddress(serverPort);

            b.option(ChannelOption.SO_KEEPALIVE, true);

            b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);


            b.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel channel) throws Exception {
                    channel.pipeline().addLast(new NettyEchoServerInHandler());
                }
            });
            ChannelFuture channelFuture = b.bind().sync();

            System.out.println("服务器启动成功：" + channelFuture.channel().localAddress());

            ChannelFuture closeFuture = channelFuture.channel().closeFuture();
            closeFuture.sync();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            workerLoopGroup.shutdownGracefully();
            bossLoopGroup.shutdownGracefully();
        }


    }

    public static void main(String[] args) {
        new NettyEchoServer(NettyDemoConfig.SOCKET_SERVER_PORT).runServer();
    }
}
