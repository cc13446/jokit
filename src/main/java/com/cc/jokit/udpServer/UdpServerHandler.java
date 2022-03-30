package com.cc.jokit.udpServer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

import java.nio.charset.StandardCharsets;

public class UdpServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private final UdpEventListener udpEventListener;


    public UdpServerHandler(UdpEventListener udpEventListener) {
        this.udpEventListener = udpEventListener;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) {
        udpEventListener.invokeIncomingListener(datagramPacket.sender(), datagramPacket.content().toString(StandardCharsets.UTF_8));
    }
}
