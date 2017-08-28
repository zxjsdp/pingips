package com.moflying;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PingController {
    public static ExecutorService cachedThreadPool = Executors.newFixedThreadPool(20);

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
    protected void pingIPsButton() {
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

        for (String ip : ips) {
            cachedThreadPool.submit(() -> pingIP(ip));
        }

        Platform.runLater(() -> this.leftResultLabel.setText("Done!"));
    }

    private void pingIP(String ip) {
        if (StringUtils.isEmpty(ip)) {
            return;
        }
        Platform.runLater(() ->
                this.rightTextArea.appendText(String.format("%s: \t%s\n", ip, CommandUtil.ping(ip))));
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
//        return MISSION_SEPARATOR + LocalDateTime.now() + MISSION_SEPARATOR;
        return MISSION_SEPARATOR;
    }
}
