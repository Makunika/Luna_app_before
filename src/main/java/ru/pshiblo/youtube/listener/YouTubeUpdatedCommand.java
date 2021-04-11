package ru.pshiblo.youtube.listener;

import com.google.api.services.youtube.model.LiveChatMessage;
import ru.pshiblo.youtube.WorkerYouTubeLiveChatInsert;
import ru.pshiblo.youtube.listener.base.YouTubeListenerListCommand;

public class YouTubeUpdatedCommand extends YouTubeListenerListCommand {

    @Override
    protected String getCommand() {
        return UpdatedCommand.getInstance().getCommand();
    }

    @Override
    protected void handleCommand(String arg, LiveChatMessage liveChatMessage) {
        WorkerYouTubeLiveChatInsert.insertMessageAsync(UpdatedCommand.getInstance().getAnswer());
    }
}
