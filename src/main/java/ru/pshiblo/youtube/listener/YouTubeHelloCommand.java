package ru.pshiblo.youtube.listener;

import com.google.api.services.youtube.model.LiveChatMessage;
import ru.pshiblo.youtube.WorkerYouTubeLiveChatInsert;
import ru.pshiblo.youtube.listener.base.YouTubeListenerListCommand;

public class YouTubeHelloCommand extends YouTubeListenerListCommand {
    @Override
    protected String getCommand() {
        return "/hello";
    }

    @Override
    protected void handleCommand(String arg, LiveChatMessage liveChatMessage) {
        WorkerYouTubeLiveChatInsert.insertMessageAsync("Привет");
    }
}
