package ru.pshiblo.youtube.listener;

import com.google.api.services.youtube.model.LiveChatMessage;
import ru.pshiblo.discord.YouTubeBot;
import ru.pshiblo.gui.views.Console;

public class YouTubeTrackCommand extends YouTubeListenerListCommand{
    @Override
    protected String getCommand() {
        return "/track";
    }

    @Override
    protected void handleCommand(String arg, LiveChatMessage liveChatMessage) {
        YouTubeBot.getListener().addToQueue(arg);
    }
}
