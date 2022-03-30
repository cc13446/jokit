package com.cc.jokit.tcpServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang3.ObjectUtils;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.*;

public class TcpServerThread implements Callable<Void> {

    private volatile Channel serverChannel;

    public void closeServer() {
        if (serverChannel != null) {
            serverChannel.close();
            serverChannel = null;
        }
    }

    private final String ip;
    private final int port;
    private final Map<InetSocketAddress, NioSocketChannel> channelMap;
    private final TcpEventListener tcpEventListener;

    public TcpServerThread(String ip, int port,  TcpEventListener tcpEventListener) {
        super();
        this.ip = ip;
        this.port = port;
        this.channelMap = new ConcurrentHashMap<>();
        this.tcpEventListener = tcpEventListener;
        this.tcpEventListener.addIncomingRichListener((channelMap::put));
        this.tcpEventListener.addLeaveListener(channelMap::remove);
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
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
                            serverSocketChannel.pipeline().addLast(new TcpServerHandler(tcpEventListener));
                        }
                    })
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        // 这个方法将自定义的ChannelHandler安装到ChannelPipeline
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            socketChannel.pipeline().addLast(new TcpServerChildrenHandler(tcpEventListener));
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

    public void disconnect(InetSocketAddress address) throws TcpServerException {
        NioSocketChannel channel = channelMap.getOrDefault(address, null);
        if (ObjectUtils.isEmpty(channel)) {
            throw new TcpServerException("TCP客户端不存在:" + address);
        }
        channel.disconnect();
    }

    public void write(InetSocketAddress address, String buffer) throws TcpServerException {
        NioSocketChannel channel = channelMap.getOrDefault(address, null);
        if (ObjectUtils.isEmpty(channel)) {
            throw new TcpServerException("TCP客户端不存在:" + address);
        }
        ChannelFuture channelFuture = channel.writeAndFlush(Unpooled.copiedBuffer(buffer, CharsetUtil.UTF_8));
        channelFuture.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()){
                tcpEventListener.invokeClientWriteCompleteListener(address, buffer);
            }else{
                tcpEventListener.invokeClientWriteUncompletedListener(address, future.cause());
            }
        });
    }
}
