package com.cc.jokit.udpServer;

import com.cc.jokit.tcpServer.TcpServerException;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang3.ObjectUtils;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class UdpServerThread implements Callable<Void> {

    private volatile Channel serverChannel;

    public void closeServer() {
        if (serverChannel != null) {
            serverChannel.close();
            serverChannel = null;
        }
    }

    private final String ip;
    private final int port;
    private final UdpEventListener udpEventListener;



    public UdpServerThread(String ip, int port, UdpEventListener udpEventListener) {
        this.ip = ip;
        this.port = port;
        this.udpEventListener = udpEventListener;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    @Override
    public Void call() throws InterruptedException {

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        try {
            // 不同的引导器决定了不同的网络行为，客户端还是服务端
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(bossGroup)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .localAddress(new InetSocketAddress(ip, port))
                    .handler(new ChannelInitializer<NioDatagramChannel>() {
                        // 监听bind行为
                        @Override
                        protected void initChannel(NioDatagramChannel nioDatagramChannel) {
                            nioDatagramChannel.pipeline().addLast(new UdpServerHandler(udpEventListener));
                        }
                    });
            ChannelFuture channelFuture = bootstrap.bind().sync();
            serverChannel = channelFuture.channel();
            serverChannel.closeFuture().sync();

        } finally {
            bossGroup.shutdownGracefully();
        }
        return null;
    }

    public void write(InetSocketAddress address, String buffer)  {

        ChannelFuture channelFuture = serverChannel.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(buffer, CharsetUtil.UTF_8), address));
        channelFuture.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()){
                udpEventListener.invokeClientWriteCompleteListener(address, buffer);
            }else{
                udpEventListener.invokeClientWriteUncompletedListener(address, future.cause());
            }
        });
    }
}
