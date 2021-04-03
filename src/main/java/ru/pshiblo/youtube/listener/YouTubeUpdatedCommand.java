package ru.pshiblo.youtube.listener;

import com.google.api.services.youtube.model.LiveChatMessage;
import ru.pshiblo.Config;
import ru.pshiblo.youtube.WorkerYouTubeLiveChatInsert;

public class YouTubeUpdatedCommand extends YouTubeListenerListCommand {

    @Override
    protected String getCommand() {
        return UpdatedCommand.getInstance().getCommand();
    }

    @Override
    protected void handleCommand(String arg, LiveChatMessage liveChatMessage) {
        WorkerYouTubeLiveChatInsert.insertMessage(UpdatedCommand.getInstance().getAnswer());
    }
}
