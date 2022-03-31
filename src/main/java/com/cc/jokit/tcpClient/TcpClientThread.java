package com.cc.jokit.tcpClient;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.util.concurrent.Callable;

public class TcpClientThread implements Callable<Void> {

    private volatile Channel channel;
    private InetSocketAddress localAddress;
    private final String remoteIp;
    private final int remotePort;
    private final TcpClientEventListener tcpClientEventListener;


    public TcpClientThread(String remoteIp, int remotePort, TcpClientEventListener tcpClientEventListener) {
        this.remoteIp = remoteIp;
        this.remotePort = remotePort;
        this.tcpClientEventListener = tcpClientEventListener;
        this.tcpClientEventListener.addSuccessConnectListener(address -> this.localAddress = address);
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
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(bossGroup)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(remoteIp, remotePort))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            socketChannel.pipeline().addLast(new TcpClientHandler(tcpClientEventListener));
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect();
            channelFuture.addListener((ChannelFutureListener) future -> {
                if (!future.isSuccess()){
                    tcpClientEventListener.invokeErrorConnectListener(future.cause());
                } else {
                    tcpClientEventListener.invokeSuccessConnectListener((InetSocketAddress)future.channel().localAddress());
                }
            });
            channelFuture.sync();
            channel = channelFuture.channel();
            // 阻塞到Channel关闭
            channel.closeFuture().sync();
        } catch (InterruptedException e){
            tcpClientEventListener.invokeErrorConnectListener(e);
        } finally {
            bossGroup.shutdownGracefully();
        }
        return null;
    }

    public void disconnect() {
        if (channel != null) {
            ChannelFuture channelFuture = channel.close();
            channelFuture.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    tcpClientEventListener.invokeDisconnectListener((InetSocketAddress) future.channel().remoteAddress());
                } else {
                    tcpClientEventListener.invokeDisconnectFailListener((InetSocketAddress) future.channel().remoteAddress(), future.cause());
                }
            });
        }
    }

    public void write(String buffer) {
        ChannelFuture channelFuture = channel.writeAndFlush(Unpooled.copiedBuffer(buffer, CharsetUtil.UTF_8));
        channelFuture.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()){
                tcpClientEventListener.invokeWriteCompleteListener((InetSocketAddress) future.channel().remoteAddress(), buffer);
            }else{
                tcpClientEventListener.invokeWriteFailListener((InetSocketAddress) future.channel().remoteAddress(), future.cause());
            }
        });
    }

    public void close() {
        if (channel != null) {
            channel.close();
            channel = null;
        }
    }
}
