package ru.pshiblo.youtube;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.LiveChatMessage;
import com.google.api.services.youtube.model.LiveChatMessageListResponse;
import com.google.api.services.youtube.model.VideoListResponse;
import ru.pshiblo.Config;
import ru.pshiblo.discord.YouTubeBot;
import ru.pshiblo.youtube.listener.YouTubeListenerList;

import java.io.IOException;
import java.util.List;

public class WorkerYouTubeLiveChatList implements Runnable {

    private final List<YouTubeListenerList> listeners;

    public WorkerYouTubeLiveChatList(List<YouTubeListenerList> listeners) {
        this.listeners = listeners;
    }

    @Override
    public void run() {
        try {
            YouTube youtubeService = YouTubeInitializer.getYoutubeService();
            while (true) {
                YouTube.LiveChatMessages.List request = youtubeService.liveChatMessages()
                        .list(Config.getInstance().getLiveChatId(), List.of("id", "snippet", "authorDetails"));

                LiveChatMessageListResponse responseLiveChat = request.execute();
                List<LiveChatMessage> messages = responseLiveChat.getItems();

                for (YouTubeListenerList listener : listeners) {
                    listener.handle(messages);
                }

                Thread.sleep(Config.getInstance().getTimeList());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
