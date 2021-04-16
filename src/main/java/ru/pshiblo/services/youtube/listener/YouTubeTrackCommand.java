package ru.pshiblo.services.youtube.listener;

import com.google.api.services.youtube.model.LiveChatMessage;
import ru.pshiblo.Config;
import ru.pshiblo.gui.log.ConsoleOut;
import ru.pshiblo.services.Context;
import ru.pshiblo.services.youtube.listener.base.YouTubeListenerCommand;

public class YouTubeTrackCommand extends YouTubeListenerCommand {
    @Override
    protected String getCommand() {
        return "/track";
    }

    @Override
    protected void handleCommand(String arg, LiveChatMessage liveChatMessage) {
        ConsoleOut.println("Запускаем трек " + arg);
        if (Config.getInstance().isDiscord()) {
            Context.getDiscordHandlerService().getListener().addToQueue(arg);
        } else {
            if (!arg.isBlank()) {
                if (arg.contains("http")) {
                    Context.getLocalAudioService().play(arg);
                } else {
                    Context.getLocalAudioService().play("ytsearch: " + arg);
                }
            }

        }

    }
}
