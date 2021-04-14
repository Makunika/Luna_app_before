package ru.pshiblo.base.init;

import com.jagrosh.jmusicbot.JMusicBot;
import ru.pshiblo.audio.LocalAudio;
import ru.pshiblo.discord.MusicExecutorBot;
import ru.pshiblo.youtube.YouTubeInitializer;

public class Initializer {

    public static void init(InitType ...args) {
        for (InitType arg : args) {
            switch (arg) {
                case LOCAL_AUDIO: {
                    LocalAudio.init();
                    break;
                }
                case YOUTUBE: {
                    YouTubeInitializer.go();
                    break;
                }
                case DISCORD_BOT: {
                    MusicExecutorBot.init();
                    JMusicBot.init();
                    break;
                }
                case HTTP_SERVER: {
                    break;
                }
            }
        }
    }

}
