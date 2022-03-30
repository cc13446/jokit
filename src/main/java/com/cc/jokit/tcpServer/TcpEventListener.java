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
    private final List<Consumer<InetSocketAddress>> leaveListener;
    private final List<BiConsumer<InetSocketAddress, String>> clientMessageListener;
    private final List<BiConsumer<InetSocketAddress, String>> clientWriteCompleteListener;
    private final List<BiConsumer<InetSocketAddress, Throwable>> clientWriteUncompletedListener;


    public TcpEventListener () {
        this.incomingListener = new LinkedList<>();
        this.incomingRichListener = new LinkedList<>();
        this.leaveListener = new LinkedList<>();
        this.clientMessageListener = new LinkedList<>();
        this.clientWriteCompleteListener = new LinkedList<>();
        this.clientWriteUncompletedListener = new LinkedList<>();

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

    public void addLeaveListener(Consumer<InetSocketAddress> consumer) {
        leaveListener.add(consumer);
    }

    public void invokeLeaveListener(InetSocketAddress address) {
        for(Consumer<InetSocketAddress> c : leaveListener) {
            c.accept(address);
        }
    }

    public void addClientMessageListener(BiConsumer<InetSocketAddress, String> consumer) {
        clientMessageListener.add(consumer);
    }

    public void invokeClientMessageListener(InetSocketAddress address, String s) {
        for(BiConsumer<InetSocketAddress, String> c : clientMessageListener) {
            c.accept(address, s);
        }
    }

    public void addClientWriteCompleteListener(BiConsumer<InetSocketAddress, String> consumer) {
        clientWriteCompleteListener.add(consumer);
    }

    public void invokeClientWriteCompleteListener(InetSocketAddress address, String s) {
        for(BiConsumer<InetSocketAddress, String> c : clientWriteCompleteListener) {
            c.accept(address, s);
        }
    }

    public void addClientWriteUncompletedListener(BiConsumer<InetSocketAddress, Throwable> consumer) {
        clientWriteUncompletedListener.add(consumer);
    }

    public void invokeClientWriteUncompletedListener(InetSocketAddress address, Throwable t) {
        for(BiConsumer<InetSocketAddress, Throwable> c : clientWriteUncompletedListener) {
            c.accept(address, t);
        }
    }
}
