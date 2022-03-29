package com.cc.jokit.tcpServer;

import com.cc.jokit.JokitException;
import io.netty.util.internal.StringUtil;

import java.util.concurrent.*;

public class TcpServer {


    private final TcpServerThread tcpServerThread;
    private final CompletionService<Void> cs;

    public TcpServer(String ip, int port) throws JokitException {
        if (StringUtil.isNullOrEmpty(ip)) {
            throw new JokitException("创建TCP服务器失败：IP为空");
        }
        if (port == 0) {
            throw new JokitException("创建TCP服务器失败：Port不规范");
        }
        this.tcpServerThread = TcpServerThread.getInstance(ip, port);
        this.cs = new ExecutorCompletionService<>(Executors.newSingleThreadExecutor());
    }

    public void start() {
        cs.submit(tcpServerThread);
    }

    public void close() throws JokitException {
        try {
            TcpServerThread.closeServer();
            cs.take().get();
        } catch (InterruptedException | ExecutionException e){
            throw new JokitException(e.getMessage());
        }
    }
}
