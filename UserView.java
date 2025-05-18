package com.project.lyricsapi;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class UserView {
    private final TextField songInput;
    private final TextField artistInput;
    private final TextArea lyricsDisplay;
    private final Label statusLabel;
    private final LyricsController controller;

    public UserView(LyricsController controller) {
        this.controller = controller;
        this.songInput = new TextField();
        this.artistInput = new TextField();
        this.lyricsDisplay = new TextArea();
        this.statusLabel = new Label();
    }

    public VBox createLayout() {
        // Create title label
        Label titleLabel = new Label("Song Lyrics Finder");
        titleLabel.setStyle("-fx-font-size: 24; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-family: 'Arial'");

        // Create input fields with better styling
        songInput.setPromptText("Enter song title...");
        songInput.setMaxWidth(300);
        songInput.setStyle("-fx-background-radius: 5; -fx-font-size: 14;");

        artistInput.setPromptText("Enter artist name...");
        artistInput.setMaxWidth(300);
        artistInput.setStyle("-fx-background-radius: 5; -fx-font-size: 14;");

        // Create search button with better styling
        Button searchButton = new Button("Search Lyrics");
        searchButton.setStyle(
            "-fx-background-color: #1DB954; " +
            "-fx-text-fill: White; " +
            "-fx-font-size: 14; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10 20; " +
            "-fx-background-radius: 5;"
        );
        searchButton.setOnAction(e -> controller.searchLyrics(songInput.getText(), artistInput.getText()));

        // Create lyrics display with better styling
        lyricsDisplay.setPrefHeight(400);
        lyricsDisplay.setMaxWidth(500);
        lyricsDisplay.setWrapText(true);
        lyricsDisplay.setEditable(false);
        lyricsDisplay.setStyle(
            "-fx-control-inner-background: #2b2b2b; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 13; " +
            "-fx-background-radius: 5; " +
            "-fx-line-spacing: -2;"
        );

        // Create status label
        statusLabel.setStyle("-fx-text-fill: #ff4444; -fx-font-size: 14;");

        // Create layout with better styling
        VBox layout = new VBox(10); // Reduced spacing between elements
        layout.getChildren().addAll(
            titleLabel,
            new Label("Song Title:") {{ setStyle("-fx-text-fill: white; -fx-font-size: 14;"); }},
            songInput,
            new Label("Artist Name:") {{ setStyle("-fx-text-fill: white; -fx-font-size: 14;"); }},
            artistInput,
            searchButton,
            lyricsDisplay,
            statusLabel
        );
        layout.setStyle("-fx-background-color: #121212;");
        layout.setPadding(new Insets(15)); // Reduced padding
        layout.setAlignment(Pos.TOP_CENTER);

        return layout;
    }

    // Getters for the controller to access UI elements
    public TextArea getLyricsDisplay() {
        return lyricsDisplay;
    }

    public Label getStatusLabel() {
        return statusLabel;
    }
}
