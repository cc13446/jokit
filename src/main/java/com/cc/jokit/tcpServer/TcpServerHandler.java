package com.cc.jokit.tcpServer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.nio.NioSocketChannel;

public class TcpServerHandler extends ChannelInboundHandlerAdapter {
    private final TcpEventListener tcpEventListener;

    public TcpServerHandler(TcpEventListener tcpEventListener) {
        this.tcpEventListener = tcpEventListener;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        NioSocketChannel socketChannel = (NioSocketChannel) msg;
        tcpEventListener.invokeIncomingListener(socketChannel.remoteAddress());
        tcpEventListener.invokeIncomingRichListener(socketChannel.remoteAddress(), socketChannel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
