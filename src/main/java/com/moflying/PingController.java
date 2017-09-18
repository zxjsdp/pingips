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
        try (AutoCloseable ac = executorService::shutdown) {
            for (String ip : ips) {
                executorService.submit(() -> pingIP(ip));
            }
            ac.close();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            executorService.shutdown();
        }

        Platform.runLater(() -> this.leftResultLabel.setText("Done!"));
    }

    @FXML
    protected void tcping() {
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
        try (AutoCloseable ac = executorService::shutdown) {
            for (SocketAddress socketAddress: socketAddresses) {
                executorService.submit(() -> tcping(socketAddress));
            }
            ac.close();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            executorService.shutdown();
        }

        Platform.runLater(() -> this.leftResultLabel.setText("Done!"));
    }

    private String pingIP(String ip) {
        if (StringUtils.isEmpty(ip)) {
            return "";
        }
        final String result = CommandUtil.ping(ip, timeoutSlider.getValue() / 1000);
        System.out.println(result);
        Platform.runLater(() -> this.rightTextArea.appendText(result + "\n"));
        return result;
    }

    private String tcping(SocketAddress socketAddress) {
        final String result = CommandUtil.tcping(
                socketAddress.getIp(),
                socketAddress.getPort(),
                (int) timeoutSlider.getValue() / 1000);
        System.out.println(result);
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
