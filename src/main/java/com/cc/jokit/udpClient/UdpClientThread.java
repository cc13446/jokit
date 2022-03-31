package com.cc.jokit.udpClient;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;

public class UdpClientThread implements Callable<Void> {

    private volatile Channel channel;
    private InetSocketAddress localAddress;
    private final String remoteIp;
    private final int remotePort;
    private final UdpClientEventListener udpClientEventListener;


    public UdpClientThread(String remoteIp, int remotePort, UdpClientEventListener udpClientEventListener) {
        this.remoteIp = remoteIp;
        this.remotePort = remotePort;
        this.udpClientEventListener = udpClientEventListener;
        this.udpClientEventListener.addSuccessBindListener(address -> this.localAddress = address);
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public InetSocketAddress getLocalAddress() {
        return localAddress;
    }

    @Override
    public Void call() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        try {
            // 不同的引导器决定了不同的网络行为，客户端还是服务端
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(bossGroup)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)

                    .remoteAddress(new InetSocketAddress(remoteIp, remotePort))
                    .handler(new ChannelInitializer<NioDatagramChannel>() {
                        @Override
                        protected void initChannel(NioDatagramChannel nioDatagramChannel) {
                            nioDatagramChannel.pipeline().addLast(new UdpClientHandler(udpClientEventListener));
                        }
                    });
            ChannelFuture channelFuture = bootstrap.bind(0);
            channelFuture.addListener(future -> {
                if(!future.isSuccess()) {
                    udpClientEventListener.invokeErrorBindListener(future.cause());
                } else {
                    udpClientEventListener.invokeSuccessBindListener((InetSocketAddress) channelFuture.channel().localAddress());
                }
            });
            channelFuture.sync();
            channel = channelFuture.channel();
            channel.closeFuture().sync();

        } catch (InterruptedException e) {
            udpClientEventListener.invokeErrorBindListener(e);
        } finally {
            bossGroup.shutdownGracefully();
        }
        return null;
    }

    public void close() {
        if (channel != null) {
            ChannelFuture channelFuture = channel.close();
            channelFuture.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    udpClientEventListener.invokeDisconnectListener(this.localAddress);
                } else {
                    udpClientEventListener.invokeDisconnectFailListener(this.localAddress, future.cause());
                }
            });
            channel = null;
        }
    }

    public void write(String buffer) {
        InetSocketAddress remoteAddress = new InetSocketAddress(remoteIp, remotePort);
        ChannelFuture channelFuture = channel.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(buffer, CharsetUtil.UTF_8), remoteAddress));
        channelFuture.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()){
                udpClientEventListener.invokeWriteCompleteListener(remoteAddress, buffer);
            }else{
                udpClientEventListener.invokeWriteFailListener(remoteAddress, future.cause());
            }
        });
    }

}
