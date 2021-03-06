package com.cc.jokit.udpServer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

import java.nio.charset.StandardCharsets;

public class UdpServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private final UdpServerEventListener udpServerEventListener;


    public UdpServerHandler(UdpServerEventListener udpServerEventListener) {
        this.udpServerEventListener = udpServerEventListener;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) {
        udpServerEventListener.invokeIncomingListener(datagramPacket.sender(), datagramPacket.content().toString(StandardCharsets.UTF_8));
    }
}
