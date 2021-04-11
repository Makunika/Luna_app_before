package ru.pshiblo.youtube;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.LiveChatMessage;
import com.google.api.services.youtube.model.LiveChatMessageListResponse;
import ru.pshiblo.Config;
import ru.pshiblo.gui.ConsoleOut;
import ru.pshiblo.youtube.listener.base.YouTubeListenerList;

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
                        .list(Config.getInstance().getLiveChatId(), List.of("snippet"));
                LiveChatMessageListResponse responseLiveChat = request.execute();
                List<LiveChatMessage> messages = responseLiveChat.getItems();

                ConsoleOut.println("Прочитано сообщений с чата: " + messages.size());
                for (YouTubeListenerList listener : listeners) {
                    listener.handle(messages);
                }
                Thread.sleep(Config.getInstance().getTimeList());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            ConsoleOut.alert(e.getMessage());
        }
    }
}
