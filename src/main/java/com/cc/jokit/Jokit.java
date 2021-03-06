package com.cc.jokit;

import com.cc.jokit.tcpClient.TcpClient;
import com.cc.jokit.tcpClient.TcpClientException;
import com.cc.jokit.tcpServer.TcpServer;
import com.cc.jokit.tcpServer.TcpServerException;
import com.cc.jokit.udpClient.UdpClient;
import com.cc.jokit.udpClient.UdpClientException;
import com.cc.jokit.udpServer.UdpServer;
import com.cc.jokit.udpServer.UdpServerException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.commons.lang3.ObjectUtils;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Jokit extends Application {

    private final static String SERVER_TCP_BIND = "TCP监听";
    private final static String SERVER_TCP_UNBIND = "TCP停止";
    private final static String SERVER_UDP_BIND = "UDP监听";
    private final static String SERVER_UDP_UNBIND = "UDP停止";

    private final static String CLIENT_TCP_CONNECT = "TCP连接";
    private final static String CLIENT_TCP_DISCONNECT = "TCP断开";
    private final static String CLIENT_UDP_CONNECT = "UDP连接";
    private final static String CLIENT_UDP_DISCONNECT = "UDP断开";

    // server
    private final static Text serverTitle = new Text("Socket服务器");

    // severInput
    // serverTCPInput
    private final static ComboBox<String> serverTcpAddrComboBox = new ComboBox<>();
    private final static TextField serverTcpPortTextField = new TextField();
    private final static Button serverTcpButton = new Button(SERVER_TCP_BIND);
    private final static HBox serverTcpHBox = new HBox();

    // serverUDPInput
    private final static ComboBox<String> serverUdpAddrComboBox = new ComboBox<>();
    private final static TextField serverUdpPortTextField = new TextField();
    private final static Button serverUdpButton = new Button(SERVER_UDP_BIND);
    private final static HBox serverUdpHBox = new HBox();

    private final static VBox serverInputVBox = new VBox();

    //server clients
    private final static Map<CheckBox, InetSocketAddress> serverTCPClients = new HashMap<>();
    private final static Map<CheckBox, InetSocketAddress> serverUDPClients = new HashMap<>();
    private final static VBox serverClientsVBox = new VBox();
    private final static ScrollPane serverClientsScrollPane = new ScrollPane();
    private final static Button serverClientsControlSelectAll = new Button("选择全部");
    private final static Button serverClientsControlDisconnect = new Button("断开连接");
    private final static VBox serverClientsControlVBox = new VBox();
    private final static HBox serverClientsHBox = new HBox();

    //server send
    //server buffer
    private final static TextField serverBufferTextField = new TextField();
    private final static Button serverBufferSendButton = new Button("发送(UTF8)");
    private final static HBox serverBufferHBox = new HBox();

    //server ascii
    private final static TextField serverBufferAsciiTextField = new TextField();
    private final static Button serverBufferAsciiSendButton = new Button("发送(ASCII)");
    private final static HBox serverBufferAsciiHBox = new HBox();

    private final static VBox serverSendVBox = new VBox();

    //server output
    private final static TextArea serverOutput = new TextArea();

    private final static VBox serverVBox = new VBox();

    // client
    private final static Text clientTitle = new Text("Socket客户端");

    // clientInput
    private final static TextField clientAddrTextField = new TextField();
    private final static TextField clientPortTextField = new TextField();
    private final static Button clientTcpButton = new Button(CLIENT_TCP_CONNECT);
    private final static Button clientUdpButton = new Button(CLIENT_UDP_CONNECT);
    private final static HBox clientInputHBox = new HBox();

    private final static VBox clientVBox = new VBox();

    //client send
    //client buffer
    private final static TextField clientBufferTextField = new TextField();
    private final static Button clientBufferSendButton = new Button("发送(UTF8)");
    private final static HBox clientBufferHBox = new HBox();

    //client ascii
    private final static TextField clientBufferAsciiTextField = new TextField();
    private final static Button clientBufferAsciiSendButton = new Button("发送(ASCII)");
    private final static HBox clientBufferAsciiHBox = new HBox();

    //client output
    private final static TextArea clientOutput = new TextArea();

    private final static VBox clientSendVBox = new VBox();

    // application
    private final static HBox hBox = new HBox();

    private static TcpServer tcpServer = null;
    private static UdpServer udpServer = null;

    private static TcpClient tcpClient = null;
    private static UdpClient udpClient = null;

    @Override
    public void start(Stage stage) {

        // server
        serverTitle.setFont(new Font(18));

        // severInput
        // serverTCPInput
        serverTcpAddrComboBox.setItems(FXCollections.observableList(Utils.getLocalIP()));
        serverTcpAddrComboBox.setPrefWidth(160);

        serverTcpPortTextField.setPrefSize(80, 20);
        serverTcpPortTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!Utils.isValidPort(newValue)) {
                serverTcpPortTextField.setText(oldValue);
            }
        });

        serverTcpButton.setMinWidth(80);
        serverTcpButton.setMinHeight(20);
        serverTcpButton.setOnMouseClicked(event -> {
            try {
                if (ObjectUtils.isEmpty(tcpServer)) {
                    String ip = serverTcpAddrComboBox.getValue();
                    int port =  Utils.parsePort(serverTcpPortTextField.getText());
                    tcpServer = new TcpServer(ip, port);
                    tcpServer.addIncomingListener(address ->
                        Platform.runLater(() -> {
                            String temp = Utils.parseHostAndPort(address.getAddress(), address.getPort());
                            CheckBox newBox = new CheckBox("[TCP]" + temp);
                            serverTCPClients.put(newBox, address);
                            serverClientsVBox.getChildren().add(newBox);
                            serverAppendLog("TCP连接:" + temp);
                        })
                    );
                    tcpServer.addLeaveListener(address ->
                        Platform.runLater(() -> {
                            String temp = Utils.parseHostAndPort(address.getAddress(), address.getPort());
                            CheckBox leave = null;
                            for (CheckBox c : serverTCPClients.keySet()) {
                                if (c.textProperty().getValue().equals("[TCP]" + temp)) {
                                    leave = c;
                                }
                            }
                            if (ObjectUtils.isNotEmpty(leave)) {
                                serverClientsVBox.getChildren().remove(leave);
                                serverTCPClients.remove(leave);
                            }
                            serverAppendLog("TCP断开:" + temp);
                        })
                    );
                    tcpServer.addClientMessageListener((address, s) ->
                        Platform.runLater(() -> {
                            String temp = Utils.parseHostAndPort(address.getAddress(), address.getPort());
                            serverAppendLog("TCP收到:" + temp + ":" + s);
                        })
                    );
                    tcpServer.addClientWriteCompleteListener((address, s) ->
                        Platform.runLater(() -> {
                            String temp = Utils.parseHostAndPort(address.getAddress(), address.getPort());
                            serverAppendLog("TCP发送:" + temp + ":" + s);
                        })
                    );
                    tcpServer.addClientWriteFailListener((address, t) ->
                        Platform.runLater(() -> {
                            String temp = Utils.parseHostAndPort(address.getAddress(), address.getPort());
                            serverAppendLog("TCP发送失败:" + temp + ":" + t.getMessage());
                        })
                    );
                    tcpServer.addErrorBindListener(error ->
                        Platform.runLater(() -> {
                            serverAppendLog("TCP监听错误:" + error.getMessage());
                            try {
                                tcpServer.close();
                            } catch (TcpServerException ex) {
                                serverAppendLog(ex.getMessage());
                            }
                            serverAppendLog("TCP停止:" + tcpServer.getIp() + ":" + tcpServer.getPort());
                            tcpServer = null;
                            serverTcpButton.setText(SERVER_TCP_BIND);
                        }
                    ));
                    tcpServer.start();
                    serverAppendLog("TCP监听:" + ip + ":" + port);
                    serverTcpButton.setText(SERVER_TCP_UNBIND);
                } else {
                    serverAppendLog("TCP停止:" + tcpServer.getIp() + ":" + tcpServer.getPort());
                    tcpServer.close();
                    tcpServer = null;
                    serverTcpButton.setText(SERVER_TCP_BIND);
                }
            } catch (TcpServerException | JokitException exception) {
                serverAppendLog(exception.getMessage());
                tcpServer = null;
                serverTcpButton.setText(SERVER_TCP_BIND);
            }
        });

        serverTcpHBox.getChildren().addAll(serverTcpAddrComboBox, serverTcpPortTextField, serverTcpButton);
        HBox.setHgrow(serverTcpPortTextField, Priority.SOMETIMES);
        serverTcpHBox.setSpacing(3);

        // serverUDPInput
        serverUdpAddrComboBox.setItems(FXCollections.observableList(Utils.getLocalIP()));
        serverUdpAddrComboBox.setPrefWidth(160);

        serverUdpPortTextField.setPrefSize(80, 20);
        serverUdpPortTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!Utils.isValidPort(newValue)) {
                serverUdpPortTextField.setText(oldValue);
            }
        });

        serverUdpButton.setMinWidth(80);
        serverUdpButton.setMinHeight(20);
        serverUdpButton.setOnMouseClicked(event -> {
            try {
                if (ObjectUtils.isEmpty(udpServer)) {
                    String ip = serverUdpAddrComboBox.getValue();
                    int port =  Utils.parsePort(serverUdpPortTextField.getText());
                    udpServer = new UdpServer(ip, port);
                    udpServer.addIncomingListener((address, s) ->
                        Platform.runLater(() -> {
                            String temp = Utils.parseHostAndPort(address.getAddress(), address.getPort());
                            if (!serverUDPClients.containsValue(address)) {
                                CheckBox newBox = new CheckBox("[UDP]" + temp);
                                serverUDPClients.put(newBox, address);
                                serverClientsVBox.getChildren().add(newBox);
                            }
                            serverAppendLog("UDP消息:" + temp + ":" + s);
                    }));
                    udpServer.addClientWriteCompleteListener((address, s) ->
                        Platform.runLater(() -> {
                            String temp = Utils.parseHostAndPort(address.getAddress(), address.getPort());
                            serverAppendLog("UDP发送:" + temp + ":" + s);
                    }));
                    udpServer.addClientWriteFailListener((address, t) ->
                        Platform.runLater(() -> {
                            String temp = Utils.parseHostAndPort(address.getAddress(), address.getPort());
                            serverAppendLog("UDP发送失败:" + temp + ":" + t.getMessage());
                    }));
                    udpServer.addErrorBindListener(error ->
                        Platform.runLater(() -> {
                            serverAppendLog("UDP监听失败:" + error);
                            try {
                                udpServer.close();
                            } catch (UdpServerException ex) {
                                serverAppendLog(ex.getMessage());
                            }
                            serverAppendLog("UDP停止:" + udpServer.getIp() + ":" + udpServer.getPort());
                            udpServer = null;
                            serverUdpButton.setText(SERVER_UDP_BIND);
                    }));
                    udpServer.start();
                    serverAppendLog("UDP监听:" + ip + ":" + port);
                    serverUdpButton.setText(SERVER_UDP_UNBIND);
                } else {
                    udpServer.close();
                    serverAppendLog("UDP停止:" + udpServer.getIp() + ":" + udpServer.getPort());
                    udpServer = null;
                    serverUdpButton.setText(SERVER_UDP_BIND);
                }
            } catch (UdpServerException | JokitException e) {
                serverAppendLog(e.getMessage());
                udpServer = null;
                serverUdpButton.setText(SERVER_UDP_BIND);
            }
        });

        serverUdpHBox.getChildren().addAll(serverUdpAddrComboBox, serverUdpPortTextField, serverUdpButton);
        HBox.setHgrow(serverUdpPortTextField, Priority.SOMETIMES);
        serverUdpHBox.setSpacing(3);

        serverInputVBox.getChildren().addAll(serverTcpHBox, serverUdpHBox);
        serverInputVBox.setSpacing(5);

        //server clients
        serverClientsVBox.getChildren().addAll(serverTCPClients.keySet());
        serverClientsVBox.setSpacing(2);

        serverClientsScrollPane.setPrefHeight(57);
        serverClientsScrollPane.setContent(serverClientsVBox);

        serverClientsControlSelectAll.setMinWidth(80);
        serverClientsControlSelectAll.setMinHeight(22);
        serverClientsControlSelectAll.setOnMouseClicked(e -> {
            for (CheckBox c : serverTCPClients.keySet()) {
                c.selectedProperty().setValue(true);
            }
            for (CheckBox c : serverUDPClients.keySet()) {
                c.selectedProperty().setValue(true);
            }
        });

        serverClientsControlDisconnect.setMinWidth(80);
        serverClientsControlDisconnect.setMinHeight(22);
        serverClientsControlDisconnect.setOnMouseClicked(event -> {
            if (ObjectUtils.isEmpty(tcpServer) || !tcpServer.isStart() && ObjectUtils.isEmpty(udpServer) || !udpServer.isStart()) {
                serverAppendLog("服务器未启动");
                return;
            }
            serverTCPClients.forEach((checkBox, address) -> {
                if (checkBox.selectedProperty().getValue()) {
                    try {
                        tcpServer.disconnect(address);
                    } catch (TcpServerException e) {
                        serverAppendLog(e.getMessage());
                    }
                }
            });
            List<CheckBox> leaves = new LinkedList<>();
            serverUDPClients.forEach((checkBox, address) -> {
                if (checkBox.selectedProperty().getValue()) {
                    serverClientsVBox.getChildren().remove(checkBox);
                    leaves.add(checkBox);
                }
            });
            leaves.forEach(serverUDPClients::remove);
        });

        serverClientsControlVBox.getChildren().addAll(serverClientsControlSelectAll, serverClientsControlDisconnect);
        serverClientsControlVBox.setSpacing(10);

        serverClientsHBox.getChildren().addAll(serverClientsScrollPane, serverClientsControlVBox);
        HBox.setHgrow(serverClientsScrollPane, Priority.SOMETIMES);
        serverClientsHBox.setSpacing(2);

        //server send
        //server buffer
        serverBufferSendButton.setMinWidth(80);
        serverBufferSendButton.setMinHeight(22);
        serverBufferSendButton.setOnMouseClicked(event -> {
            if (!ObjectUtils.isEmpty(tcpServer) && tcpServer.isStart()) {
                serverTCPClients.forEach((checkBox, address) -> {
                    if (checkBox.selectedProperty().getValue()) {
                        try {
                            tcpServer.write(address, serverBufferTextField.getText());
                        } catch (TcpServerException e) {
                            serverAppendLog(e.getMessage());
                        }
                    }
                });
            }
            if (!ObjectUtils.isEmpty(udpServer) && udpServer.isStart()) {
                serverUDPClients.forEach((checkBox, address) -> {
                    if (checkBox.selectedProperty().getValue()) {
                        try {
                            udpServer.write(address, serverBufferTextField.getText());
                        } catch (TcpServerException e) {
                            serverAppendLog(e.getMessage());
                        }
                    }
                });
            }
        });

        serverBufferHBox.getChildren().addAll(serverBufferTextField, serverBufferSendButton);
        serverBufferHBox.setSpacing(2);
        HBox.setHgrow(serverBufferTextField, Priority.SOMETIMES);

        //server ascii
        serverBufferAsciiSendButton.setMinWidth(80);
        serverBufferAsciiSendButton.setMinHeight(22);
        serverBufferAsciiSendButton.setOnMouseClicked(event -> {
            try {
                String buffer = Utils.asciiToString(serverBufferAsciiTextField.getText());
                if (!ObjectUtils.isEmpty(tcpServer) && tcpServer.isStart()) {
                    serverTCPClients.forEach((checkBox, address) -> {
                        if (checkBox.selectedProperty().getValue()) {
                            try {
                                tcpServer.write(address, buffer);
                            } catch (TcpServerException e) {
                                serverAppendLog(e.getMessage());
                            }
                        }
                    });
                }
                if (!ObjectUtils.isEmpty(udpServer) && udpServer.isStart()) {
                    serverUDPClients.forEach((checkBox, address) -> {
                        if (checkBox.selectedProperty().getValue()) {
                            try {
                                udpServer.write(address, buffer);
                            } catch (TcpServerException e) {
                                serverAppendLog(e.getMessage());
                            }
                        }
                    });
                }
            } catch (JokitException e) {
                serverAppendLog(e.getMessage());
            }
        });

        serverBufferAsciiHBox.getChildren().addAll(serverBufferAsciiTextField, serverBufferAsciiSendButton);
        serverBufferAsciiHBox.setSpacing(2);
        HBox.setHgrow(serverBufferAsciiTextField, Priority.SOMETIMES);

        serverSendVBox.getChildren().addAll(serverBufferHBox, serverBufferAsciiHBox);
        serverSendVBox.setSpacing(5);

        //server output
        serverOutput.setEditable(false);
        serverOutput.setMaxHeight(Double.POSITIVE_INFINITY);

        serverVBox.getChildren().addAll(serverTitle, serverInputVBox, serverClientsHBox, serverSendVBox, serverOutput);
        VBox.setVgrow(serverOutput, Priority.SOMETIMES);
        serverVBox.setPadding(new Insets(20));
        serverVBox.setSpacing(15);
        serverVBox.setAlignment(Pos.TOP_CENTER);


        // client
        clientTitle.setFont(new Font(18));

        // clientInput
        clientAddrTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!Utils.isValidIP(newValue)) {
                clientAddrTextField.setText(oldValue);
            }
        });

        clientPortTextField.setPrefSize(60, 20);
        clientPortTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!Utils.isValidPort(newValue)) {
                clientPortTextField.setText(oldValue);
            }
        });

        clientTcpButton.setMinWidth(70);
        clientTcpButton.setMinHeight(20);
        clientTcpButton.setOnMouseClicked(event -> {
            try {
                if (ObjectUtils.isEmpty(tcpClient)) {
                    if(ObjectUtils.isNotEmpty(udpClient)) {
                        udpClient.close();
                        udpClient = null;
                        clientUdpButton.setText(CLIENT_UDP_CONNECT);
                    }
                    String ip = clientAddrTextField.getText();
                    int port = Utils.parsePort(clientPortTextField.getText());
                    tcpClient = new TcpClient(ip, port);
                    tcpClient.addSuccessConnectListener(address -> Platform.runLater(() -> {
                        String temp = Utils.parseHostAndPort(address.getAddress(), address.getPort());
                        clientAppendLog("TCP连接成功 本地地址:" + temp + " " + "远端地址" + tcpClient.getRemoteIp() + ":" + tcpClient.getRemotePort());
                    }));
                    tcpClient.addErrorConnectListener(error -> Platform.runLater(() -> {
                        clientAppendLog("TCP连接失败:" + error.getMessage());
                        try {
                            tcpClient.close();
                        } catch (TcpClientException e) {
                            clientAppendLog(e.getMessage());
                        }
                        tcpClient = null;
                        clientTcpButton.setText(CLIENT_TCP_CONNECT);
                    }));
                    tcpClient.addMessageListener((address, s) -> Platform.runLater(() -> {
                        String temp = Utils.parseHostAndPort(address.getAddress(), address.getPort());
                        clientAppendLog("TCP收到:" + temp + ":" + s);
                    }));
                    tcpClient.addWriteCompleteListener((address, s) -> Platform.runLater(() -> {
                        String temp = Utils.parseHostAndPort(address.getAddress(), address.getPort());
                        clientAppendLog("TCP发送:" + temp + ":" + s);
                    }));
                    tcpClient.addWriteFailListener((address, throwable) -> Platform.runLater(() -> {
                        String temp = Utils.parseHostAndPort(address.getAddress(), address.getPort());
                        clientAppendLog("TCP发送失败:" + temp + ":" + throwable.getMessage());
                    }));
                    tcpClient.addDisconnectListener(address -> Platform.runLater(() ->{
                        String temp = Utils.parseHostAndPort(address.getAddress(), address.getPort());
                        clientAppendLog("TCP断开成功 本地地址:" + temp);
                    }));
                    tcpClient.addDisconnectFailListener((address, throwable) -> Platform.runLater(() -> {
                        String temp = Utils.parseHostAndPort(address.getAddress(), address.getPort());
                        clientAppendLog("TCP断开失败 本地地址:" + temp + ":" + throwable.getMessage());
                    }));
                    tcpClient.connect();
                    clientTcpButton.setText(CLIENT_TCP_DISCONNECT);
                } else {
                    tcpClient.close();
                    tcpClient = null;
                    clientTcpButton.setText(CLIENT_TCP_CONNECT);
                }
            } catch (UdpClientException e) {
                clientAppendLog(e.getMessage());
                udpClient = null;
                clientUdpButton.setText(CLIENT_UDP_CONNECT);
            } catch (JokitException | TcpClientException e) {
                clientAppendLog(e.getMessage());
                tcpClient = null;
                clientTcpButton.setText(CLIENT_TCP_CONNECT);
            }
        });

        clientUdpButton.setMinWidth(70);
        clientUdpButton.setMinHeight(20);
        clientUdpButton.setOnMouseClicked(event -> {
            try {
                if (ObjectUtils.isEmpty(udpClient)) {
                    if(ObjectUtils.isNotEmpty(tcpClient)) {
                        tcpClient.close();
                        tcpClient = null;
                        clientTcpButton.setText(CLIENT_TCP_CONNECT);
                    }
                    String ip = clientAddrTextField.getText();
                    int port = Utils.parsePort(clientPortTextField.getText());
                    udpClient = new UdpClient(ip, port);
                    udpClient.addSuccessBindListener(address -> Platform.runLater(() -> {
                        String temp = Utils.parseHostAndPort(address.getAddress(), address.getPort());
                        clientAppendLog("UDP连接成功 本地地址:" + temp + " " + "远端地址" + udpClient.getRemoteIp() + ":" + udpClient.getRemotePort());
                    }));
                    udpClient.addErrorBindListener(error -> Platform.runLater(() -> {
                        clientAppendLog("UDP连接失败:" + error.getMessage());
                        try {
                            udpClient.close();
                        } catch (UdpClientException e) {
                            clientAppendLog(e.getMessage());
                        }
                        udpClient = null;
                        clientUdpButton.setText(CLIENT_UDP_CONNECT);
                    }));
                    udpClient.addMessageListener((address, s) -> Platform.runLater(() -> {
                        String temp = Utils.parseHostAndPort(address.getAddress(), address.getPort());
                        clientAppendLog("UDP收到:" + temp + ":" + s);
                    }));
                    udpClient.addWriteCompleteListener((address, s) -> Platform.runLater(() -> {
                        String temp = Utils.parseHostAndPort(address.getAddress(), address.getPort());
                        clientAppendLog("UDP发送:" + temp + ":" + s);
                    }));
                    udpClient.addWriteFailListener((address, throwable) -> Platform.runLater(() -> {
                        String temp = Utils.parseHostAndPort(address.getAddress(), address.getPort());
                        clientAppendLog("UDP发送失败:" + temp + ":" + throwable.getMessage());
                    }));
                    udpClient.addDisconnectListener(address -> Platform.runLater(() ->{
                        String temp = Utils.parseHostAndPort(address.getAddress(), address.getPort());
                        clientAppendLog("UDP关闭成功 本地地址:" + temp);
                    }));
                    udpClient.addDisconnectFailListener((address, throwable) -> Platform.runLater(() -> {
                        String temp = Utils.parseHostAndPort(address.getAddress(), address.getPort());
                        clientAppendLog("UDP关闭失败 本地地址:" + temp + ":" + throwable.getMessage());
                    }));
                    udpClient.bind();
                    clientUdpButton.setText(CLIENT_UDP_DISCONNECT);
                } else {
                    udpClient.close();
                    udpClient = null;
                    clientUdpButton.setText(CLIENT_UDP_CONNECT);
                }
            } catch (TcpClientException e) {
                clientAppendLog(e.getMessage());
                tcpClient = null;
                clientTcpButton.setText(CLIENT_TCP_CONNECT);
            } catch (JokitException |  UdpClientException e) {
                clientAppendLog(e.getMessage());
                udpClient = null;
                clientUdpButton.setText(CLIENT_UDP_CONNECT);
            }
        });

        clientInputHBox.getChildren().addAll(clientAddrTextField, clientPortTextField, clientTcpButton, clientUdpButton);
        HBox.setHgrow(clientAddrTextField, Priority.SOMETIMES);
        clientInputHBox.setSpacing(2);

        //client send
        //client buffer
        clientBufferSendButton.setMinWidth(80);
        clientBufferSendButton.setMinHeight(22);
        clientBufferSendButton.setOnMouseClicked(event -> {
            if (ObjectUtils.isNotEmpty(tcpClient) && tcpClient.isStart()) {
                tcpClient.write(clientBufferTextField.getText());
            }
            if (ObjectUtils.isNotEmpty(udpClient) && udpClient.isStart()) {
                udpClient.write(clientBufferTextField.getText());
            }
        });
        clientBufferHBox.getChildren().addAll(clientBufferTextField, clientBufferSendButton);
        clientBufferHBox.setSpacing(2);
        HBox.setHgrow(clientBufferTextField, Priority.SOMETIMES);

        //client ascii
        clientBufferAsciiSendButton.setMinWidth(80);
        clientBufferAsciiSendButton.setMinHeight(22);
        clientBufferAsciiSendButton.setOnMouseClicked(event -> {
            try {
                String temp = Utils.asciiToString(clientBufferAsciiTextField.getText());
                if (ObjectUtils.isNotEmpty(tcpClient) && tcpClient.isStart()) {
                    tcpClient.write(temp);
                }
                if (ObjectUtils.isNotEmpty(udpClient) && udpClient.isStart()) {
                    udpClient.write(temp);
                }
            } catch (JokitException e) {
               clientAppendLog(e.getMessage());
            }
        });
        clientBufferAsciiHBox.getChildren().addAll(clientBufferAsciiTextField, clientBufferAsciiSendButton);
        clientBufferAsciiHBox.setSpacing(2);
        HBox.setHgrow(clientBufferAsciiTextField, Priority.SOMETIMES);

        clientSendVBox.getChildren().addAll(clientBufferHBox, clientBufferAsciiHBox);
        clientSendVBox.setSpacing(5);

        //client output
        clientOutput.setEditable(false);
        clientOutput.setMaxHeight(Double.POSITIVE_INFINITY);

        clientVBox.getChildren().addAll(clientTitle, clientInputHBox, clientSendVBox, clientOutput);
        VBox.setVgrow(clientOutput, Priority.SOMETIMES);
        clientVBox.setPadding(new Insets(20));
        clientVBox.setSpacing(15);
        clientVBox.setAlignment(Pos.TOP_CENTER);

        // application
        hBox.getChildren().addAll(serverVBox, clientVBox);
        hBox.setFillHeight(true);
        HBox.setHgrow(serverVBox, Priority.SOMETIMES);
        HBox.setHgrow(clientVBox, Priority.SOMETIMES);
        hBox.setAlignment(Pos.TOP_CENTER);
        hBox.setMinWidth(800);

        Scene scene = new Scene(hBox);
        stage.setScene(scene);
        stage.setTitle("Jokit");
        stage.setHeight(500);
        stage.setWidth(800);
        stage.setMinHeight(500);
        stage.setMinWidth(800);
        stage.setOnCloseRequest(windowEvent -> {
            if (ObjectUtils.isNotEmpty(tcpServer)) {
                try {
                    tcpServer.close();
                    tcpServer = null;
                } catch (TcpServerException e) {
                    e.printStackTrace();
                }
            }
            if (ObjectUtils.isNotEmpty(udpServer)) {
                try {
                    udpServer.close();
                    udpServer = null;
                } catch (UdpServerException e) {
                    e.printStackTrace();
                }
            }
            if (ObjectUtils.isNotEmpty(tcpClient)) {
                try {
                    tcpClient.close();
                    tcpClient = null;
                } catch (TcpClientException e) {
                    e.printStackTrace();
                }

            }
            if (ObjectUtils.isNotEmpty(udpClient)) {
                try {
                    udpClient.close();
                    udpClient = null;
                } catch (UdpClientException e) {
                    e.printStackTrace();
                }

            }
        });
        stage.show();
    }

    public void serverAppendLog(String msg) {

        serverOutput.appendText(Utils.generateLog(msg));
    }

    public void clientAppendLog(String msg) {

        clientOutput.appendText(Utils.generateLog(msg));
    }

}