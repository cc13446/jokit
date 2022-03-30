package com.cc.jokit.udpServer;

import com.cc.jokit.tcpServer.TcpServer;
import com.cc.jokit.tcpServer.TcpServerException;
import io.netty.util.internal.StringUtil;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

public class UdpServer {

    enum State {
        READY, START, CLOSE
    }

    private final UdpServerThread udpServerThread;
    private final UdpEventListener udpEventListener;
    private final CompletionService<Void> completionService;
    private volatile State serverState;



    public UdpServer(String ip, int port) throws UdpServerException {
        if (StringUtil.isNullOrEmpty(ip)) {
            throw new UdpServerException("创建TCP服务器失败：IP为空");
        }
        if (port < 0 || port > 65535 ) {
            throw new UdpServerException("创建TCP服务器失败：Port不规范");
        }

        this.completionService = new ExecutorCompletionService<>(Executors.newSingleThreadExecutor());
        this.udpEventListener = new UdpEventListener();
        this.udpServerThread = new UdpServerThread(ip, port, this.udpEventListener);
        this.serverState = State.READY;
    }

    public String getIp() {
        return udpServerThread.getIp();
    }
    public int getPort() {
        return udpServerThread.getPort();
    }

    public void start() {
        completionService.submit(udpServerThread);
        this.serverState = State.START;
    }

    public void close() throws UdpServerException {
        try {
            this.serverState = State.CLOSE;
            udpServerThread.closeServer();
            completionService.take().get();
        } catch (InterruptedException | ExecutionException e){
            throw new UdpServerException(e.getMessage());
        }
    }

    public boolean isStart() {
        return serverState == State.START;
    }

    public void write(InetSocketAddress address, String buffer) throws TcpServerException {
        if(StringUtil.isNullOrEmpty(buffer)) return;
        udpServerThread.write(address, buffer);
    }

    private void checkStateBeforeAddListener() throws UdpServerException {
        if(this.serverState != State.READY) throw new UdpServerException("服务器运行中，不允许添加Listener");
    }

    public void addIncomingListener(BiConsumer<InetSocketAddress, String> c) throws UdpServerException {
        checkStateBeforeAddListener();
        udpEventListener.addIncomingListener(c);
    }

    public void addClientWriteCompleteListener(BiConsumer<InetSocketAddress, String> consumer) throws UdpServerException {
        checkStateBeforeAddListener();
        udpEventListener.addClientWriteCompleteListener(consumer);
    }

    public void addClientWriteUncompletedListener(BiConsumer<InetSocketAddress, Throwable> consumer) throws UdpServerException {
        checkStateBeforeAddListener();
        udpEventListener.addClientWriteUncompletedListener(consumer);
    }


}
