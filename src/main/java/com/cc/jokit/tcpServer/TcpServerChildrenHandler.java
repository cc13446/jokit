package com.cc.jokit.tcpServer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

// ChannelInboundHandler 接受入站事件
@ChannelHandler.Sharable
public class TcpServerChildrenHandler extends ChannelDuplexHandler {

    private final TcpEventListener tcpEventListener;

    public TcpServerChildrenHandler(TcpEventListener tcpEventListener) {
        this.tcpEventListener = tcpEventListener;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf byteBuf = (ByteBuf) msg;
        tcpEventListener.invokeClientMessageListener((InetSocketAddress) ctx.channel().remoteAddress(), byteBuf.toString(CharsetUtil.UTF_8));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        tcpEventListener.invokeLeaveListener((InetSocketAddress) ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
