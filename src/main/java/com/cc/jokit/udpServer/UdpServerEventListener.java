package com.cc.jokit.udpServer;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class UdpServerEventListener {
    private final List<BiConsumer<InetSocketAddress, String>> incomingListener;
    private final List<BiConsumer<InetSocketAddress, String>> clientWriteCompleteListener;
    private final List<BiConsumer<InetSocketAddress, Throwable>> clientWriteFailListener;
    private final List<Consumer<Throwable>> errorBindListener;

    public UdpServerEventListener() {
        this.incomingListener = new LinkedList<>();
        this.clientWriteCompleteListener = new LinkedList<>();
        this.clientWriteFailListener = new LinkedList<>();
        this.errorBindListener = new LinkedList<>();
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

    public void addClientWriteFailListener(BiConsumer<InetSocketAddress, Throwable> consumer) {
        clientWriteFailListener.add(consumer);
    }

    public void invokeClientWriteFailListener(InetSocketAddress address, Throwable t) {
        for(BiConsumer<InetSocketAddress, Throwable> c : clientWriteFailListener) {
            c.accept(address, t);
        }
    }

    public void addErrorBindListener(Consumer<Throwable> c) {
        errorBindListener.add(c);
    }

    public void invokeErrorBindListener(Throwable e) {
        for(Consumer<Throwable> c : errorBindListener) {
            c.accept(e);
        }
    }
}
