package com.project.lyricsapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class GeniusInteractor {
    private final OkHttpClient client;
    private final ObjectMapper mapper;
    private static final String GENIUS_API_BASE_URL = "https://api.genius.com";
    private final String apiToken;

    public GeniusInteractor(String apiToken) {
        this.client = new OkHttpClient();
        this.mapper = new ObjectMapper();
        this.apiToken = apiToken;
    }

    /**
     * Search for a song in the Genius API
     * @param song The song title
     * @param artist The artist name
     * @return The lyrics URL if found, null otherwise
     * @throws IOException If there's an error communicating with the API
     */
    private String searchSong(String song, String artist) throws IOException {
        String searchQuery = URLEncoder.encode(song + " " + artist, StandardCharsets.UTF_8);
        String searchUrl = GENIUS_API_BASE_URL + "/search?q=" + searchQuery;
        
        Request request = new Request.Builder()
            .url(searchUrl)
            .addHeader("Authorization", "Bearer " + apiToken)
            .build();
            
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "No error details";
                throw new IOException("API request failed with code " + response.code() + ": " + errorBody);
            }
            
            String responseBody = response.body().string();
            JsonNode root = mapper.readTree(responseBody);
            
            if (root.has("error")) {
                throw new IOException("API Error: " + root.path("error").asText() + 
                    " - " + root.path("error_description").asText());
            }
            
            JsonNode hits = root.path("response").path("hits");
            
            if (hits.size() > 0) {
                String path = hits.get(0).path("result").path("path").asText();
                return "https://genius.com" + path;
            }
            return null;
        }
    }

    /**
     * Fetch lyrics from a Genius URL
     * @param lyricsUrl The URL of the lyrics page
     * @return Lyrics object if found, null otherwise
     * @throws IOException If there's an error fetching the lyrics
     */
    private LyricsModel fetchLyrics(String lyricsUrl) throws IOException {
        try {
            Document doc = Jsoup.connect(lyricsUrl)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                .get();
            
            Element lyricsContainer = doc.selectFirst("div[class^=Lyrics__Container]");
            if (lyricsContainer == null) {
                lyricsContainer = doc.selectFirst("[class^=lyrics]");
            }
            if (lyricsContainer == null) {
                lyricsContainer = doc.selectFirst("[class*=Lyrics__Root]");
            }
            
            if (lyricsContainer != null) {
                // Remove unwanted sections
                lyricsContainer.select("div.Contributors").remove();
                lyricsContainer.select("div.RightSidebar__Container").remove();
                lyricsContainer.select("div.Lyrics__Footer").remove();
                lyricsContainer.select("div[class*=Translation__Container]").remove();
                
                // Clean and format the lyrics
                String content = lyricsContainer.html()
                    .replaceAll("<br>", "\n")
                    .replaceAll("</div>", "\n")
                    .replaceAll("<.*?>", "")
                    .replaceAll("\\s*\\n\\s*", "\n")
                    .replaceAll("\\n{3,}", "\n\n")
                    .trim();

                // Add space after section headers
                content = content.replaceAll("\\[(.*?)\\]", "\n[$1]\n");
                
                // Ensure consistent spacing between verses
                content = content.replaceAll("\\n{2,}", "\n\n");
                content = content.replaceAll("(?m)^\\[(.*?)\\]\\s*$", "\n[$1]");
                content = content.replaceAll("\\n{3,}", "\n\n");

                // Get song title and artist
                String title = null;
                String artist = null;
                Element titleElement = doc.selectFirst("h1[class*=SongHeader__Title]");
                Element artistElement = doc.selectFirst("a[class*=SongHeader__Artist]");
                
                if (titleElement != null) {
                    title = titleElement.text();
                }
                if (artistElement != null) {
                    artist = artistElement.text();
                }

                return new LyricsModel(title, artist, content.trim(), lyricsUrl);
            }
            return null;
        } catch (IOException e) {
            throw new IOException("Failed to fetch lyrics: " + e.getMessage());
        }
    }

    /**
     * Search and fetch lyrics for a song
     * @param song The song title
     * @param artist The artist name
     * @return LyricsResult containing the lyrics and any error message
     */
    public LyricsResult getLyrics(String song, String artist) {
        try {
            String lyricsUrl = searchSong(song, artist);
            if (lyricsUrl == null) {
                return new LyricsResult(null, "No lyrics found for this song");
            }

            LyricsModel lyrics = fetchLyrics(lyricsUrl);
            if (lyrics == null) {
                return new LyricsResult(
                    "Lyrics not found on the page. You can view them at:\n" + lyricsUrl,
                    null
                );
            }

            return new LyricsResult(lyrics.getFormattedLyrics(), null);
        } catch (IOException e) {
            return new LyricsResult(null, "Error: " + e.getMessage());
        }
    }

    /**
         * Class to hold the result of a lyrics search
         */
        public record LyricsResult(String lyrics, String errorMessage) {

        public boolean isSuccess() {
                return errorMessage == null;
            }
        }
}
