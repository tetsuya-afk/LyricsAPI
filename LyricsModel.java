package com.project.lyricsapi;

/**
 * Model class representing lyrics data
 */
public record LyricsModel(String title, String artist, String content, String sourceUrl) {

    /**
     * Returns a formatted string representation of the lyrics
     * including title, artist, and content
     */
    public String getFormattedLyrics() {
        StringBuilder formatted = new StringBuilder();
        if (title != null && !title.isEmpty()) {
            formatted.append(title).append("\n");
        }
        if (artist != null && !artist.isEmpty()) {
            formatted.append("by ").append(artist).append("\n\n");
        }
        if (content != null && !content.isEmpty()) {
            formatted.append(content);
        }
        return formatted.toString();
    }
} 