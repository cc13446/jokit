package com.cc.jokit.udpClient;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class UdpClientEventListener {


    private final List<BiConsumer<InetSocketAddress, String>> messageListener;
    private final List<Consumer<InetSocketAddress>> disconnectListener;
    private final List<BiConsumer<InetSocketAddress, Throwable>> disconnectFailListener;

    private final List<BiConsumer<InetSocketAddress, String>> writeCompleteListener;
    private final List<BiConsumer<InetSocketAddress, Throwable>>writeFailListener;
    private final List<Consumer<Throwable>> errorBindListener;
    private final List<Consumer<InetSocketAddress>> successBindListener;

    public UdpClientEventListener() {
        this.messageListener = new LinkedList<>();
        this.disconnectListener = new LinkedList<>();
        this.disconnectFailListener = new LinkedList<>();
        this.writeCompleteListener = new LinkedList<>();
        this.writeFailListener = new LinkedList<>();
        this.errorBindListener = new LinkedList<>();
        this.successBindListener = new LinkedList<>();
    }

    public void addMessageListener(BiConsumer<InetSocketAddress, String> c) {
         messageListener.add(c);
    }


    public void invokeMessageListener(InetSocketAddress remoteAddress, String s) {
        for(BiConsumer<InetSocketAddress, String> c : messageListener) {
            c.accept(remoteAddress, s);
        }
    }

    public void addDisconnectListener(Consumer<InetSocketAddress> c) {
        disconnectListener.add(c);
    }

    public void invokeDisconnectListener(InetSocketAddress address) {
        for(Consumer<InetSocketAddress> c : disconnectListener) {
            c.accept(address);
        }
    }

    public void addDisconnectFailListener(BiConsumer<InetSocketAddress, Throwable> c) {
        disconnectFailListener.add(c);
    }

    public void invokeDisconnectFailListener(InetSocketAddress address, Throwable cause) {
        for(BiConsumer<InetSocketAddress, Throwable> c : disconnectFailListener) {
            c.accept(address, cause);
        }
    }

    public void addWriteCompleteListener(BiConsumer<InetSocketAddress, String> c) {
        writeCompleteListener.add(c);
    }

    public void invokeWriteCompleteListener(InetSocketAddress address, String buffer) {
        for(BiConsumer<InetSocketAddress, String> c : writeCompleteListener) {
            c.accept(address, buffer);
        }
    }

    public void addWriteFailListener(BiConsumer<InetSocketAddress, Throwable> c) {
        writeFailListener.add(c);
    }

    public void invokeWriteFailListener(InetSocketAddress address, Throwable cause) {
        for(BiConsumer<InetSocketAddress, Throwable> c : writeFailListener) {
            c.accept(address, cause);
        }
    }

    public void addErrorBindListener(Consumer<Throwable> c) {
        errorBindListener.add(c);
    }

    public void invokeErrorBindListener(Throwable cause) {
        for(Consumer<Throwable> c : errorBindListener) {
            c.accept(cause);
        }
    }

    public void addSuccessBindListener(Consumer<InetSocketAddress> c) {
        successBindListener.add(c);
    }

    public void invokeSuccessBindListener(InetSocketAddress localAddress) {
        for(Consumer<InetSocketAddress> c : successBindListener) {
            c.accept(localAddress);
        }
    }
}
