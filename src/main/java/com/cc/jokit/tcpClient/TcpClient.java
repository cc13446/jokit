package com.cc.jokit.tcpClient;

import io.netty.util.internal.StringUtil;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class TcpClient {

    enum State {
        READY, START, CLOSE
    }

    private final TcpClientThread tcpClientThread;
    private final CompletionService<Void> completionService;
    private final TcpClientEventListener tcpClientEventListener;
    private volatile State serverState;


    public TcpClient(String ip, int port) throws TcpClientException {
        if (StringUtil.isNullOrEmpty(ip)) {
            throw new TcpClientException("创建TCP客户端失败：IP为空");
        }
        if (port < 0 || port > 65535 ) {
            throw new TcpClientException("创建TCP客户端失败：Port不规范");
        }

        this.completionService = new ExecutorCompletionService<>(Executors.newSingleThreadExecutor());
        this.tcpClientEventListener = new TcpClientEventListener();
        this.tcpClientThread = new TcpClientThread(ip, port, this.tcpClientEventListener);
        this.serverState = State.READY;
    }

    public String getRemoteIp() {
        return tcpClientThread.getRemoteIp();
    }
    public int getRemotePort() {
        return tcpClientThread.getRemotePort();
    }

    public InetSocketAddress getLocalAddress() {
        return tcpClientThread.getLocalAddress();
    }

    public void connect() {
        completionService.submit(tcpClientThread);
        this.serverState = State.START;
    }

    public void close() throws TcpClientException {
        try {
            if(isStart()) {
                this.serverState = State.CLOSE;
                tcpClientThread.disconnect();
                tcpClientThread.close();
                completionService.take().get();
            }
        } catch (InterruptedException | ExecutionException e){
            throw new TcpClientException("TCP关闭错误:" + e.getMessage());
        }
    }

    public boolean isStart() {
        return serverState == State.START;
    }

    public void write(String buffer) {
        if(StringUtil.isNullOrEmpty(buffer)) return;
        tcpClientThread.write(buffer);
    }

    public void addMessageListener(BiConsumer<InetSocketAddress, String> c) {
        this.tcpClientEventListener.addMessageListener(c);
    }

    public void addDisconnectListener(Consumer<InetSocketAddress> c) {
        this.tcpClientEventListener.addDisconnectListener(c);
    }

    public void addDisconnectFailListener(BiConsumer<InetSocketAddress, Throwable> c) {
        this.tcpClientEventListener.addDisconnectFailListener(c);
    }

    public void addWriteCompleteListener(BiConsumer<InetSocketAddress, String> c) {
        this.tcpClientEventListener.addWriteCompleteListener(c);
    }

    public void addWriteFailListener(BiConsumer<InetSocketAddress, Throwable> c) {
        this.tcpClientEventListener.addWriteFailListener(c);
    }

    public void addErrorConnectListener(Consumer<Throwable> c) {
        this.tcpClientEventListener.addErrorConnectListener(c);
    }

    public void addSuccessConnectListener(Consumer<InetSocketAddress> c) {
        this.tcpClientEventListener.addSuccessConnectListener(c);
    }
}
