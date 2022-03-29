package com.cc.jokit;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.LinkedList;
import java.util.List;

public class Jokit extends Application {

    // server
    public final static Text serverTitle = new Text("Socket服务器");

    // severInput
    // serverTCPInput
    public final static TextField serverTcpAddrTextField = new TextField();
    public final static TextField serverTcpPortTextField = new TextField();
    public final static Button serverTcpButton = new Button("TCP服务器");
    public final static HBox serverTcpHBox = new HBox();

    // serverUDPInput
    public final static TextField serverUdpAddrTextField = new TextField();
    public final static TextField serverUdpPortTextField = new TextField();
    public final static Button serverUdpButton = new Button("UDP服务器");
    public final static HBox serverUdpHBox = new HBox();

    public final static VBox serverInputVBox = new VBox();

    //server clients
    public final static List<CheckBox> serverClients = new LinkedList<>();
    public final static VBox serverClientsVBox = new VBox();
    public final static ScrollPane serverClientsScrollPane = new ScrollPane();
    public final static Button serverClientsControlSelectAll = new Button("选择全部");
    public final static Button serverClientsControlDisconnect = new Button("断开连接");
    public final static VBox serverClientsControlVBox = new VBox();
    public final static HBox serverClientsHBox = new HBox();

    //server send
    //server buffer
    public final static TextField serverBufferTextField = new TextField();
    public final static Button serverBufferSendButton = new Button("发送(UTF8)");
    public final static HBox serverBufferHBox = new HBox();

    //server ascii
    public final static TextField serverBufferAsciiTextField = new TextField();
    public final static Button serverBufferAsciiSendButton = new Button("发送(ASCII)");
    public final static HBox serverBufferAsciiHBox = new HBox();

    public final static VBox serverSendVBox = new VBox();

    //server output
    public final static TextField serverOutput = new TextField();

    public final static VBox serverVBox = new VBox();

    // client
    public final static Text clientTitle = new Text("Socket客户端");

    // clientInput
    public final static TextField clientAddrTextField = new TextField();
    public final static TextField clientPortTextField = new TextField();
    public final static Button clientTcpButton = new Button("TCP连接");
    public final static Button clientUdpButton = new Button("Udp连接");
    public final static HBox clientInputHBox = new HBox();

    public final static VBox clientVBox = new VBox();

    //client send
    //client buffer
    public final static TextField clientBufferTextField = new TextField();
    public final static Button clientBufferSendButton = new Button("发送(UTF8)");
    public final static HBox clientBufferHBox = new HBox();

    //client ascii
    public final static TextField clientBufferAsciiTextField = new TextField();
    public final static Button clientBufferAsciiSendButton = new Button("发送(ASCII)");
    public final static HBox clientBufferAsciiHBox = new HBox();

    //client output
    public final static TextField clientOutput = new TextField();

    public final static VBox clientSendVBox = new VBox();

    // application
    public final static HBox hBox = new HBox();

    @Override
    public void start(Stage stage) {

        // server
        serverTitle.setFont(new Font(18));

        // severInput
        // serverTCPInput
        serverTcpAddrTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!Utils.isValidIP(newValue)) {
                serverTcpAddrTextField.setText(oldValue);
            }
        });

        serverTcpPortTextField.setPrefSize(80, 20);
        serverTcpPortTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!Utils.isValidPort(newValue)) {
                serverTcpPortTextField.setText(oldValue);
            }
        });

        serverTcpButton.setMinWidth(80);
        serverTcpButton.setMinHeight(20);

        serverTcpHBox.getChildren().addAll(serverTcpAddrTextField, serverTcpPortTextField, serverTcpButton);
        HBox.setHgrow(serverTcpAddrTextField, Priority.SOMETIMES);
        serverTcpHBox.setSpacing(3);

        // serverUDPInput
        serverUdpAddrTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!Utils.isValidIP(newValue)) {
                serverUdpAddrTextField.setText(oldValue);
            }
        });

        serverUdpPortTextField.setPrefSize(80, 20);
        serverUdpPortTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!Utils.isValidPort(newValue)) {
                serverUdpPortTextField.setText(oldValue);
            }
        });

        serverUdpButton.setMinWidth(80);
        serverUdpButton.setMinHeight(20);

        serverUdpHBox.getChildren().addAll(serverUdpAddrTextField, serverUdpPortTextField, serverUdpButton);
        HBox.setHgrow(serverUdpAddrTextField, Priority.SOMETIMES);
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
}