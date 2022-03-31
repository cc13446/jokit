package com.cc.jokit.udpClient;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class UdpClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private final UdpClientEventListener udpClientEventListener;

    public UdpClientHandler(UdpClientEventListener udpClientEventListener)  {
        this.udpClientEventListener = udpClientEventListener;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) {
        udpClientEventListener.invokeMessageListener(datagramPacket.sender(), datagramPacket.content().toString(StandardCharsets.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
