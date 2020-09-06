package com.pakzaban.lexy;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("View.fxml"));
        primaryStage.setTitle("Pong");
        primaryStage.setScene(new Scene(root, 800, 800));
        primaryStage.show();
    }



    public static void main(String[] args) {
        launch(args);
    }
}
