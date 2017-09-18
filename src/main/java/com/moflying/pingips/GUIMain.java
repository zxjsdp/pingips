package com.moflying.pingips;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GUIMain extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/ping.fxml"));
        primaryStage.setTitle("Ping IPs");
        primaryStage.setScene(new Scene(root, 1000, 700));
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
    }

    public static void main(String[] args) {
        launch(args);
    }
}
