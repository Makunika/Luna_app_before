package ru.pshiblo.services.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import ru.pshiblo.services.ServiceThread;
import ru.pshiblo.services.ServiceType;

import javax.security.auth.login.LoginException;

public class DiscordHandlerBot extends ServiceThread {

    private Listener listener;
    private JDA jda;

    public Listener getListener() {
        return listener;
    }

    @Override
    protected void runInThread() {
        try {
            listener = new Listener();
            jda = JDABuilder.createLight("ODI2NTQ1MDUyMzcyMzAzOTMy.YGOCEA.zc67zrvyyFM9bhvxxch5DhVk1y0", GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_VOICE_STATES)
                    .addEventListeners(listener)
                    .setActivity(Activity.listening("to jams"))
                    .setStatus(OnlineStatus.DO_NOT_DISTURB)
                    .enableCache(CacheFlag.VOICE_STATE)
                    .build();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void shutdown() {
        listener = null;
        if (jda != null) {
            jda.shutdown();
        }
        super.shutdown();
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.DISCORD_HANDLER;
    }
}
