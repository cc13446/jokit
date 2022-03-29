package com.cc.jokit.tcpServer;

import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class TcpEventListener {
    private final List<Consumer<InetSocketAddress>> incomingListener;
    private final List<BiConsumer<InetSocketAddress, NioSocketChannel>> incomingRichListener;

    public TcpEventListener () {
        this.incomingListener = new LinkedList<>();
        this.incomingRichListener = new LinkedList<>();
    }

    public void addIncomingListener(Consumer<InetSocketAddress> consumer) {
        incomingListener.add(consumer);
    }

    public void invokeIncomingListener(InetSocketAddress address) {
        for(Consumer<InetSocketAddress> c : incomingListener) {
            c.accept(address);
        }
    }

    public void addIncomingRichListener(BiConsumer<InetSocketAddress, NioSocketChannel> consumer) {
        incomingRichListener.add(consumer);
    }

    public void invokeIncomingRichListener(InetSocketAddress address, NioSocketChannel nioSocketChannel) {
        for(BiConsumer<InetSocketAddress, NioSocketChannel> c : incomingRichListener) {
            c.accept(address, nioSocketChannel);
        }
    }
}
