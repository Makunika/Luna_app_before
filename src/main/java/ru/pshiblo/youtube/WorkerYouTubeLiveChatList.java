package ru.pshiblo.youtube;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.LiveChatMessage;
import com.google.api.services.youtube.model.LiveChatMessageListResponse;
import com.google.api.services.youtube.model.VideoListResponse;
import ru.pshiblo.Config;
import ru.pshiblo.discord.YouTubeBot;

import java.io.IOException;
import java.util.List;

public class WorkerYouTubeLiveChatList implements Runnable {

    @Override
    public void run() {
        try {
            YouTube youtubeService = YouTubeInitializer.getYoutubeService();
            while (true) {
                YouTube.LiveChatMessages.List request = youtubeService.liveChatMessages()
                        .list(Config.getInstance().getLiveChatId(), List.of("id", "snippet", "authorDetails"));

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
                Thread.sleep(10000);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
