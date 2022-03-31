package com.cc.jokit.tcpClient;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

public class TcpClientHandler extends ChannelDuplexHandler {

    private final TcpClientEventListener tcpClientEventListener;

    public TcpClientHandler(TcpClientEventListener tcpClientEventListener)  {
        this.tcpClientEventListener = tcpClientEventListener;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf byteBuf = (ByteBuf) msg;
        tcpClientEventListener.invokeMessageListener((InetSocketAddress) ctx.channel().remoteAddress(), byteBuf.toString(CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
