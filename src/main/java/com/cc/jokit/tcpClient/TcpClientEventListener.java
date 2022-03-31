package com.cc.jokit.tcpClient;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class TcpClientEventListener {


    private final List<BiConsumer<InetSocketAddress, String>> messageListener;
    private final List<Consumer<InetSocketAddress>> disconnectListener;
    private final List<BiConsumer<InetSocketAddress, Throwable>> disconnectFailListener;

    private final List<BiConsumer<InetSocketAddress, String>> writeCompleteListener;
    private final List<BiConsumer<InetSocketAddress, Throwable>>writeFailListener;
    private final List<Consumer<Throwable>> errorConnectListener;
    private final List<Consumer<InetSocketAddress>> successConnectListener;

    public TcpClientEventListener() {
        this.messageListener = new LinkedList<>();
        this.disconnectListener = new LinkedList<>();
        this.disconnectFailListener = new LinkedList<>();
        this.writeCompleteListener = new LinkedList<>();
        this.writeFailListener = new LinkedList<>();
        this.errorConnectListener = new LinkedList<>();
        this.successConnectListener = new LinkedList<>();
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

    public void addErrorConnectListener(Consumer<Throwable> c) {
        errorConnectListener.add(c);
    }

    public void invokeErrorConnectListener(Throwable cause) {
        for(Consumer<Throwable> c : errorConnectListener) {
            c.accept(cause);
        }
    }

    public void addSuccessConnectListener(Consumer<InetSocketAddress> c) {
        successConnectListener.add(c);
    }

    public void invokeSuccessConnectListener(InetSocketAddress localAddress) {
        for(Consumer<InetSocketAddress> c : successConnectListener) {
            c.accept(localAddress);
        }
    }
}
