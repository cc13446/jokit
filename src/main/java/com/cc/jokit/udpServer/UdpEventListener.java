package com.cc.jokit.udpServer;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;

public class UdpEventListener {
    private List<BiConsumer<InetSocketAddress, String>> incomingListener;
    private final List<BiConsumer<InetSocketAddress, String>> clientWriteCompleteListener;
    private final List<BiConsumer<InetSocketAddress, Throwable>> clientWriteUncompletedListener;

    public UdpEventListener() {
        this.incomingListener = new LinkedList<>();
        this.clientWriteCompleteListener = new LinkedList<>();
        this.clientWriteUncompletedListener = new LinkedList<>();
    }

    public void addIncomingListener(BiConsumer<InetSocketAddress, String> c) {
        incomingListener.add(c);
    }

    public void invokeIncomingListener(InetSocketAddress address, String s) {
        for(BiConsumer<InetSocketAddress, String> c : incomingListener) {
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
