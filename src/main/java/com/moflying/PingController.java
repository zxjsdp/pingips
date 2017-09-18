package com.moflying;

import com.moflying.struct.SocketAddress;
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
    private static final String MISSION_SEPARATOR = "\n===================================\n";

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
            this.leftResultLabel.setText("Ping start...");
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

            this.rightTextArea.appendText(generateTimeInfo());

            ExecutorService executorService = Executors.newFixedThreadPool(50);
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
            this.leftResultLabel.setText("TCPing start...");
            String content = this.leftTextArea.getText();
            if (StringUtils.isEmpty(content)) {
                Platform.runLater(() -> this.leftResultLabel.setText("Blank content!"));
            }

            content = content.trim();
            List<String> ipAndPorts = Arrays.asList(content.split("\\n"));
            if (CollectionUtils.isEmpty(ipAndPorts)) {
                Platform.runLater(() -> this.leftResultLabel.setText("Blank content!"));
                return;
            }
            List<SocketAddress> socketAddresses = SocketAddress.toSocketAddresses(ipAndPorts);

            this.rightTextArea.appendText(generateTimeInfo());

            ExecutorService executorService = Executors.newFixedThreadPool(20);
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

    private static String generateTimeInfo() {
        return MISSION_SEPARATOR;
    }
}
