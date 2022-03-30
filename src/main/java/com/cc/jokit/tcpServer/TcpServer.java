package com.cc.jokit.tcpServer;

import io.netty.util.internal.StringUtil;

import java.net.InetSocketAddress;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class TcpServer {

    enum State {
        READY, START, CLOSE
    }

    private final TcpServerThread tcpServerThread;
    private final CompletionService<Void> completionService;
    private final TcpEventListener tcpEventListener;
    private volatile State serverState;


    public TcpServer(String ip, int port) throws TcpServerException {
        if (StringUtil.isNullOrEmpty(ip)) {
            throw new TcpServerException("创建TCP服务器失败：IP为空");
        }
        if (port < 0 || port > 65535 ) {
            throw new TcpServerException("创建TCP服务器失败：Port不规范");
        }

        this.completionService = new ExecutorCompletionService<>(Executors.newSingleThreadExecutor());
        this.tcpEventListener = new TcpEventListener();
        this.tcpServerThread = new TcpServerThread(ip, port, this.tcpEventListener);
        this.serverState = State.READY;
    }

    public String getIp() {
        return tcpServerThread.getIp();
    }
    public int getPort() {
        return tcpServerThread.getPort();
    }

    public void start() {
        completionService.submit(tcpServerThread);
        this.serverState = State.START;
    }

    public void close() throws TcpServerException {
        try {
            if(isStart()) {
                this.serverState = State.CLOSE;
                tcpServerThread.closeServer();
                completionService.take().get();
            }
        } catch (InterruptedException | ExecutionException e){
            throw new TcpServerException("TCP关闭错误:" + e.getMessage());
        }
    }

    public boolean isStart() {
        return serverState == State.START;
    }

    public void disconnect(InetSocketAddress address) throws TcpServerException {
        tcpServerThread.disconnect(address);
    }
    public void write(InetSocketAddress address, String buffer) throws TcpServerException {
        if(StringUtil.isNullOrEmpty(buffer)) return;
        tcpServerThread.write(address, buffer);
    }

    private void checkStateBeforeAddListener() throws TcpServerException {
        if(this.serverState != State.READY) throw new TcpServerException("服务器运行中，不允许添加Listener");
    }

    public void addIncomingListener(Consumer<InetSocketAddress> consumer) throws TcpServerException {
        checkStateBeforeAddListener();
        tcpEventListener.addIncomingListener(consumer);
    }

    public void addLeaveListener(Consumer<InetSocketAddress> consumer) throws TcpServerException {
        checkStateBeforeAddListener();
        tcpEventListener.addLeaveListener(consumer);
    }

    public void addClientMessageListener(BiConsumer<InetSocketAddress, String> consumer) throws TcpServerException {
        checkStateBeforeAddListener();
        tcpEventListener.addClientMessageListener(consumer);
    }

    public void addClientWriteCompleteListener(BiConsumer<InetSocketAddress, String> consumer) throws TcpServerException {
        checkStateBeforeAddListener();
        tcpEventListener.addClientWriteCompleteListener(consumer);
    }

    public void addClientWriteUncompletedListener(BiConsumer<InetSocketAddress, Throwable> consumer) throws TcpServerException {
        checkStateBeforeAddListener();
        tcpEventListener.addClientWriteUncompletedListener(consumer);
    }

    public void addErrorBindListener(Consumer<Throwable> consumer) throws TcpServerException {
        checkStateBeforeAddListener();
        tcpEventListener.addErrorBindListener(consumer);
    }

}
