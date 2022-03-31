package com.cc.jokit.udpClient;

import io.netty.util.internal.StringUtil;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class UdpClient {

    enum State {
        READY, START, CLOSE
    }

    private final UdpClientThread udpClientThread;
    private final CompletionService<Void> completionService;
    private final UdpClientEventListener udpClientEventListener;
    private volatile State serverState;


    public UdpClient(String ip, int port) throws UdpClientException {
        if (StringUtil.isNullOrEmpty(ip)) {
            throw new UdpClientException("创建TCP服务器失败：IP为空");
        }
        if (port < 0 || port > 65535 ) {
            throw new UdpClientException("创建TCP服务器失败：Port不规范");
        }

        this.completionService = new ExecutorCompletionService<>(Executors.newSingleThreadExecutor());
        this.udpClientEventListener = new UdpClientEventListener();
        this.udpClientThread = new UdpClientThread(ip, port, this.udpClientEventListener);
        this.serverState = State.READY;
    }

    public String getRemoteIp() {
        return udpClientThread.getRemoteIp();
    }
    public int getRemotePort() {
        return udpClientThread.getRemotePort();
    }

    public InetSocketAddress getLocalAddress() {
        return udpClientThread.getLocalAddress();
    }

    public void bind() {
        completionService.submit(udpClientThread);
        this.serverState = State.START;
    }

    public void close() throws UdpClientException {
        try {
            if(isStart()) {
                this.serverState = State.CLOSE;
                udpClientThread.close();
                completionService.take().get();
            }
        } catch (InterruptedException | ExecutionException e){
            throw new UdpClientException("TCP关闭错误:" + e.getMessage());
        }
    }

    public boolean isStart() {
        return serverState == State.START;
    }

    public void write(String buffer) {
        if(StringUtil.isNullOrEmpty(buffer)) return;
        udpClientThread.write(buffer);
    }

    public void addMessageListener(BiConsumer<InetSocketAddress, String> c) {
        this.udpClientEventListener.addMessageListener(c);
    }

    public void addDisconnectListener(Consumer<InetSocketAddress> c) {
        this.udpClientEventListener.addDisconnectListener(c);
    }

    public void addDisconnectFailListener(BiConsumer<InetSocketAddress, Throwable> c) {
        this.udpClientEventListener.addDisconnectFailListener(c);
    }

    public void addWriteCompleteListener(BiConsumer<InetSocketAddress, String> c) {
        this.udpClientEventListener.addWriteCompleteListener(c);
    }

    public void addWriteFailListener(BiConsumer<InetSocketAddress, Throwable> c) {
        this.udpClientEventListener.addWriteFailListener(c);
    }

    public void addErrorBindListener(Consumer<Throwable> c) {
        this.udpClientEventListener.addErrorBindListener(c);
    }

    public void addSuccessBindListener(Consumer<InetSocketAddress> c) {
        this.udpClientEventListener.addSuccessBindListener(c);
    }
}
