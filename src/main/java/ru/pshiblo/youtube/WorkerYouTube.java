package ru.pshiblo.youtube;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.LiveChatMessage;
import com.google.api.services.youtube.model.LiveChatMessageListResponse;
import com.google.api.services.youtube.model.VideoListResponse;
import ru.pshiblo.Config;
import ru.pshiblo.discord.YouTubeBot;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

public class WorkerYouTube implements Runnable {

    private static final String CLIENT_SECRETS= "/client_secret.json";
    private static final Collection<String> SCOPES =
            Collections.singletonList("https://www.googleapis.com/auth/youtube.readonly");

    private static final String APPLICATION_NAME = "API code samples";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /**
     * Create an authorized Credential object.
     *
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize(final NetHttpTransport httpTransport) throws IOException {
        // Load client secrets.
        InputStream in = WorkerYouTube.class.getResourceAsStream(CLIENT_SECRETS);
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                        .build();
        Credential credential =
                new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        return credential;
    }

    /**
     * Build and return an authorized API client service.
     *
     * @return an authorized API client service
     * @throws GeneralSecurityException, IOException
     */
    public static YouTube getService() throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = authorize(httpTransport);
        return new YouTube.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static void go() {
        new Thread(new WorkerYouTube()).start();
    }

    @Override
    public void run() {
        try {
            YouTube youtubeService = getService();
            YouTube.Videos.List requestVideos = youtubeService.videos()
                    .list(List.of("snippet","contentDetails","statistics", "liveStreamingDetails"));
            VideoListResponse response = requestVideos.setId(List.of(Config.getInstance().getVideoId())).execute();

            while(true) {
                YouTube.LiveChatMessages.List request = youtubeService.liveChatMessages()
                        .list(response.getItems().get(0).getLiveStreamingDetails().getActiveLiveChatId(), List.of("id", "snippet", "authorDetails"));

                LiveChatMessageListResponse responseLiveChat = request.execute();

                List<LiveChatMessage> messages = responseLiveChat.getItems();
                for (int i = messages.size() - 1; i >= 0; i--) {
                    LiveChatMessage liveChatMessage = messages.get(i);
                    String messageText = liveChatMessage.getSnippet().getTextMessageDetails().getMessageText();
                    if (messageText.matches("/track .*")) {
                        if (YouTubeBot.getListener().play(messageText.substring("/track ".length()), liveChatMessage.getSnippet().getPublishedAt())) {
                            System.out.println("Новое сообщение от: " + liveChatMessage.getAuthorDetails().getDisplayName() + ". Команда: " + messageText + ", Время: " + liveChatMessage.getSnippet().getPublishedAt());
                        }
                        //YouTubeBot.getListener().play("https://www.youtube.com/watch?v=3HrSVXP99kQ", liveChatMessage.getSnippet().getPublishedAt());
                        break;
                    }
                }
                Thread.sleep(20000);
            }
        } catch (GeneralSecurityException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
