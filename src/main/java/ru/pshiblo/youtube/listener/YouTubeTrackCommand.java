package ru.pshiblo.youtube.listener;

import com.google.api.services.youtube.model.LiveChatMessage;
import ru.pshiblo.Config;
import ru.pshiblo.audio.LocalAudio;
import ru.pshiblo.discord.YouTubeBot;
import ru.pshiblo.gui.ConsoleOut;
import ru.pshiblo.youtube.listener.base.YouTubeListenerListCommand;

public class YouTubeTrackCommand extends YouTubeListenerListCommand {
    @Override
    protected String getCommand() {
        return "/track";
    }

    @Override
    protected void handleCommand(String arg, LiveChatMessage liveChatMessage) {
        ConsoleOut.println("Запускаем трек " + arg);
        if (Config.getInstance().isDiscord()) {
            YouTubeBot.getListener().addToQueue(arg);
        } else {
            if (!arg.isBlank()) {
                if (arg.contains("http")) {
                    LocalAudio.play(arg);
                } else {
                    LocalAudio.play("ytsearch: " + arg);
                }
            }

        }

    }
}
