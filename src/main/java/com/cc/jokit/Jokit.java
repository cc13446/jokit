package com.cc.jokit;

import com.cc.jokit.tcpServer.TcpServer;
import javafx.application.Application;
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

import java.util.LinkedList;
import java.util.List;

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
    private final static List<CheckBox> serverClients = new LinkedList<>();
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
    private final static TextField serverOutput = new TextField();

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
    private final static TextField clientOutput = new TextField();

    private final static VBox clientSendVBox = new VBox();

    // application
    private final static HBox hBox = new HBox();

    private static TcpServer tcpServer = null;

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
        serverTcpButton.setOnMouseClicked(e -> {
            if (null == tcpServer) {
                try {
                    tcpServer = new TcpServer(serverTcpAddrComboBox.getValue(), Utils.parsePort(serverTcpPortTextField.getText()));
                    tcpServer.start();
                } catch (JokitException exception) {
                    serverAppendLog(exception.getMessage());
                }
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

        serverUdpHBox.getChildren().addAll(serverUdpAddrComboBox, serverUdpPortTextField, serverUdpButton);
        HBox.setHgrow(serverUdpPortTextField, Priority.SOMETIMES);
        serverUdpHBox.setSpacing(3);

        serverInputVBox.getChildren().addAll(serverTcpHBox, serverUdpHBox);
        serverInputVBox.setSpacing(5);

        //server clients
        serverClientsVBox.getChildren().addAll(serverClients);
        serverClientsVBox.setSpacing(2);

        serverClientsScrollPane.setPrefHeight(57);
        serverClientsScrollPane.setContent(serverClientsVBox);

        serverClientsControlSelectAll.setMinWidth(80);
        serverClientsControlSelectAll.setMinHeight(22);

        serverClientsControlDisconnect.setMinWidth(80);
        serverClientsControlDisconnect.setMinHeight(22);

        serverClientsControlVBox.getChildren().addAll(serverClientsControlSelectAll, serverClientsControlDisconnect);
        serverClientsControlVBox.setSpacing(10);

        serverClientsHBox.getChildren().addAll(serverClientsScrollPane, serverClientsControlVBox);
        HBox.setHgrow(serverClientsScrollPane, Priority.SOMETIMES);
        serverClientsHBox.setSpacing(2);

        //server send
        //server buffer
        serverBufferSendButton.setMinWidth(80);
        serverBufferSendButton.setMinHeight(22);
        serverBufferHBox.getChildren().addAll(serverBufferTextField, serverBufferSendButton);
        serverBufferHBox.setSpacing(2);
        HBox.setHgrow(serverBufferTextField, Priority.SOMETIMES);

        //server ascii
        serverBufferAsciiSendButton.setMinWidth(80);
        serverBufferAsciiSendButton.setMinHeight(22);
        serverBufferAsciiHBox.getChildren().addAll(serverBufferAsciiTextField, serverBufferAsciiSendButton);
        serverBufferAsciiHBox.setSpacing(2);
        HBox.setHgrow(serverBufferAsciiTextField, Priority.SOMETIMES);

        serverSendVBox.getChildren().addAll(serverBufferHBox, serverBufferAsciiHBox);
        serverSendVBox.setSpacing(5);

        //server output
        serverOutput.setDisable(true);
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

        clientTcpButton.setMinWidth(50);
        clientTcpButton.setMinHeight(20);

        clientUdpButton.setMinWidth(50);
        clientUdpButton.setMinHeight(20);

        clientInputHBox.getChildren().addAll(clientAddrTextField, clientPortTextField, clientTcpButton, clientUdpButton);
        HBox.setHgrow(clientAddrTextField, Priority.SOMETIMES);
        clientInputHBox.setSpacing(2);

        //client send
        //client buffer
        clientBufferSendButton.setMinWidth(80);
        clientBufferSendButton.setMinHeight(22);
        clientBufferHBox.getChildren().addAll(clientBufferTextField, clientBufferSendButton);
        clientBufferHBox.setSpacing(2);
        HBox.setHgrow(clientBufferTextField, Priority.SOMETIMES);

        //client ascii
        clientBufferAsciiSendButton.setMinWidth(80);
        clientBufferAsciiSendButton.setMinHeight(22);
        clientBufferAsciiHBox.getChildren().addAll(clientBufferAsciiTextField, clientBufferAsciiSendButton);
        clientBufferAsciiHBox.setSpacing(2);
        HBox.setHgrow(clientBufferAsciiTextField, Priority.SOMETIMES);

        clientSendVBox.getChildren().addAll(clientBufferHBox, clientBufferAsciiHBox);
        clientSendVBox.setSpacing(5);

        //client output
        clientOutput.setDisable(true);
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
        stage.setMinHeight(500);
        stage.setMinWidth(800);
        stage.show();
    }

    public void serverAppendLog(String log) {
        String old = serverOutput.getText();
        serverOutput.setText(old + "\n" + log);
    }

}