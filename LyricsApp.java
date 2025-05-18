package com.project.lyricsapi;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LyricsApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        LyricsController controller = new LyricsController();
        Scene scene = new Scene(controller.createLayout(), 550, 700);
        
        primaryStage.setTitle("Lyrics Finder");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
