package ru.pshiblo.discord;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class MusicExecutorBot {

    private static Listener listener;

    public static Listener getListener() {
        return listener;
    }

    public static void init() {
        try {
            listener = new Listener();
            JDABuilder.createLight("ODI2NTQ1MDUyMzcyMzAzOTMy.YGOCEA.zc67zrvyyFM9bhvxxch5DhVk1y0", GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_VOICE_STATES)
                    .addEventListeners(listener)
                    .setActivity(Activity.listening("to jams"))
                    .setStatus(OnlineStatus.DO_NOT_DISTURB)
                    .enableCache(CacheFlag.VOICE_STATE)
                    .build();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }
}
