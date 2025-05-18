package com.project.lyricsapi;

import javafx.scene.layout.VBox;
import javafx.application.Platform;

public class LyricsController {
    private final GeniusInteractor geniusInteractor;
    private final UserView view;
    // Get your Client Access Token from https://genius.com/api-clients
    // It should look like: YourActualClientAccessTokenHere123456...
    private static final String GENIUS_ACCESS_TOKEN = "RPQ8r4nuap3P0ZSgczBB42JVst4g-6u94cF2V1dvkNRBsrTs2XFikMh5d9Fz8z9x";

    public LyricsController() {
        this.geniusInteractor = new GeniusInteractor(GENIUS_ACCESS_TOKEN);
        this.view = new UserView(this);
    }

    public VBox createLayout() {
        return view.createLayout();
    }

    public void searchLyrics(String song, String artist) {
        song = song.trim();
        artist = artist.trim();
        
        if (song.isEmpty() || artist.isEmpty()) {
            view.getStatusLabel().setText("Please enter both song and artist name");
            return;
        }

        // Clear previous results and show loading message
        view.getLyricsDisplay().setText("Searching for lyrics...");
        view.getStatusLabel().setText("");
        
        // Run the API call in a background thread
        String finalSong = song;
        String finalArtist = artist;
        new Thread(() -> {
            GeniusInteractor.LyricsResult result = geniusInteractor.getLyrics(finalSong, finalArtist);
            
            Platform.runLater(() -> {
                if (result.isSuccess()) {
                    view.getLyricsDisplay().setText(result.lyrics());
                    view.getStatusLabel().setText("");
                } else {
                    view.getLyricsDisplay().setText("");
                    view.getStatusLabel().setText(result.errorMessage());
                }
            });
        }).start();
    }
}