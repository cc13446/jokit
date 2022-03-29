package com.cc.jokit.tcpServer;

import io.netty.util.internal.StringUtil;

import java.net.InetSocketAddress;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class TcpServer {

    enum State {
        READY, START, CLOSE
    }

    private final TcpServerThread tcpServerThread;
    private final CompletionService<Void> completionService;
    private final TcpEventListener tcpEventListener;
    private State serverState;


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

    public void start() {
        this.serverState = State.START;
        completionService.submit(tcpServerThread);
    }

    public void close() throws TcpServerException {
        try {
            tcpServerThread.closeServer();
            completionService.take().get();
            this.serverState = State.CLOSE;
        } catch (InterruptedException | ExecutionException e){
            throw new TcpServerException(e.getMessage());
        }
    }

    private void checkStateBeforeAddListener() throws TcpServerException {
        if(this.serverState != State.READY) throw new TcpServerException("服务器运行中，不允许添加Listener");
    }

    public void addIncomingListener(Consumer<InetSocketAddress> consumer) throws TcpServerException {
        checkStateBeforeAddListener();
        tcpEventListener.addIncomingListener(consumer);
    }
}
