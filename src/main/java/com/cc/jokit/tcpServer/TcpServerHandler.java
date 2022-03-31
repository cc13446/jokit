package com.cc.jokit.tcpServer;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.nio.NioSocketChannel;

public class TcpServerHandler extends ChannelDuplexHandler {
    private final TcpServerEventListener tcpServerEventListener;

    public TcpServerHandler(TcpServerEventListener tcpServerEventListener) {
        this.tcpServerEventListener = tcpServerEventListener;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        NioSocketChannel socketChannel = (NioSocketChannel) msg;
        tcpServerEventListener.invokeIncomingListener(socketChannel.remoteAddress());
        tcpServerEventListener.invokeIncomingRichListener(socketChannel.remoteAddress(), socketChannel);

        ctx.fireChannelRead(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
