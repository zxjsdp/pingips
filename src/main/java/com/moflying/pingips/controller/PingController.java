package com.moflying.pingips.controller;

import com.moflying.pingips.struct.SocketAddress;
import com.moflying.pingips.utils.CommandUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PingController {
    private static final String PING_RESULT_SEPARATOR = "\n================ PING ================\n";
    private static final String TCPING_RESULT_SEPARATOR = "\n================ TCPING ================\n";

    public static final int THREAD_POOL_SIZE = 30;

    @FXML
    private Button pingButton;
    @FXML
    private TextArea leftTextArea;
    @FXML
    private TextArea rightTextArea;
    @FXML
    private Label leftResultLabel;
    @FXML
    private Label rightResultLabel;
    @FXML
    private Slider timeoutSlider;

    @FXML
    protected void ping() {
        Executors.newSingleThreadExecutor().submit(() -> {
            Platform.runLater(() -> this.leftResultLabel.setText("Ping start..."));
            String content = this.leftTextArea.getText();
            if (StringUtils.isEmpty(content)) {
                Platform.runLater(() -> this.leftResultLabel.setText("Blank content!"));
                return;
            }

            content = content.trim();

            List<String> ips = Arrays.asList(content.split("\\n"));
            if (CollectionUtils.isEmpty(ips)) {
                Platform.runLater(() -> this.leftResultLabel.setText("Blank content!"));
                return;
            }

            this.rightTextArea.appendText(PING_RESULT_SEPARATOR);

            ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
            for (String ip : ips) {
                executorService.submit(() -> ping(ip));
            }
            executorService.shutdown();
            try {
                executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                Platform.runLater(() -> this.leftResultLabel.setText(String.format("Ping all %d IPs Done!", ips.size())));
            } catch (InterruptedException e) {
                Platform.runLater(() -> this.leftResultLabel.setText("Terminated!"));
            }
        });
    }

    @FXML
    protected void tcping() {
        Executors.newSingleThreadExecutor().submit(() -> {
            Platform.runLater(() -> this.leftResultLabel.setText("TCPing start..."));
            String content = this.leftTextArea.getText();
            if (StringUtils.isEmpty(content)) {
                Platform.runLater(() -> this.leftResultLabel.setText("Blank content!"));
                return;
            }

            content = content.trim();
            List<String> ipAndPorts = Arrays.asList(content.split("\\n"));
            if (CollectionUtils.isEmpty(ipAndPorts)) {
                Platform.runLater(() -> this.leftResultLabel.setText("Blank content!"));
                return;
            }
            List<SocketAddress> socketAddresses = SocketAddress.toSocketAddresses(ipAndPorts);

            this.rightTextArea.appendText(TCPING_RESULT_SEPARATOR);

            ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
            for (SocketAddress socketAddress : socketAddresses) {
                executorService.submit(() -> tcping(socketAddress));
            }
            executorService.shutdown();
            try {
                executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                Platform.runLater(() ->
                        this.leftResultLabel.setText(String.format("TCPing all %d IPs Done!", socketAddresses.size())));
            } catch (InterruptedException e) {
                Platform.runLater(() -> this.leftResultLabel.setText("Terminated!"));
            }
        });
    }

    private String ping(String ip) {
        if (StringUtils.isEmpty(ip)) {
            return "";
        }
        final String result = CommandUtil.ping(ip, timeoutSlider.getValue() / 1000);
        System.out.println(result);
        Platform.runLater(() -> this.leftResultLabel.setText(ip));
        Platform.runLater(() -> this.rightTextArea.appendText(result + "\n"));
        return result;
    }

    private String tcping(SocketAddress socketAddress) {
        final String result = CommandUtil.tcping(
                socketAddress.getIp(),
                socketAddress.getPort(),
                (int) timeoutSlider.getValue() / 1000);
        System.out.println(result);
        Platform.runLater(() -> this.leftResultLabel.setText(socketAddress.toString()));
        Platform.runLater(() -> this.rightTextArea.appendText(result + "\n"));
        return result;
    }

    @FXML
    protected void clearLeftTextArea() {
        this.leftTextArea.setText("");
        this.leftResultLabel.setText("Cleared!");
    }

    @FXML
    protected void clearRightTextArea() {
        this.rightTextArea.setText("");
        this.rightResultLabel.setText("Cleared!");
    }
}
