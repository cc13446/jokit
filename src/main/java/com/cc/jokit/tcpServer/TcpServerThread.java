package com.cc.jokit.tcpServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.Callable;

public class TcpServerThread implements Callable<Void> {

    private static volatile TcpServerThread tcpServerThread;

    private static volatile Channel serverChannel;

    private final String ip;
    private final int port;


    private TcpServerThread(String ip, int port) {
        super();
        this.ip = ip;
        this.port = port;
    }

    public static TcpServerThread getInstance(String ip, int port) {
        if (tcpServerThread == null) {
            synchronized (TcpServerThread.class) {
                if (tcpServerThread == null) {
                    tcpServerThread = new TcpServerThread(ip, port);
                }
            }
        }
        return tcpServerThread;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public static void closeServer() {
        if (serverChannel != null) {
            serverChannel.close();
            serverChannel = null;
        }
    }

    @Override
    public Void call() throws InterruptedException {

        // 两个group 一个负责监听bind 一个负责读取socket消息
        // 包含多个EventLoop, 每个EventLoop与一个线程绑定
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 不同的引导器决定了不同的网络行为，客户端还是服务端
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(ip, port))
                    .handler(new ChannelInitializer<ServerSocketChannel>() {
                        // 监听bind行为
                        @Override
                        protected void initChannel(ServerSocketChannel serverSocketChannel) {
                            serverSocketChannel.pipeline().addLast(new TcpServerHandler());
                        }
                    })
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        // 这个方法将自定义的ChannelHandler安装到ChannelPipeline
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            socketChannel.pipeline().addLast(new TcpServerChildrenHandler());
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind().sync();
            serverChannel = channelFuture.channel();
            // 阻塞到Channel关闭
            serverChannel.closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
        return null;
    }
}
