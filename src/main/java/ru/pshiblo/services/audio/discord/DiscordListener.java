package ru.pshiblo.services.audio.discord;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;
import ru.pshiblo.Config;
import ru.pshiblo.services.audio.AudioFactory;

import java.util.List;

public class DiscordListener extends ListenerAdapter {

    private boolean isInit;
    private MessageChannel messageChannel;

    public DiscordListener() {
        AudioFactory.getInstance().setDiscordConfig();
    }


    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (!isInit) {
            Message msg = event.getMessage();
            Guild guild = event.getGuild();

            if (msg.getContentRaw().startsWith("!connect ")) {
                String arg = msg.getContentRaw().substring("!connect ".length());
                List<VoiceChannel> channels = guild.getVoiceChannelsByName(arg, true);
                if (!channels.isEmpty()) {
                    VoiceChannel channel = channels.get(0);
                    AudioManager audioManager = channel.getGuild().getAudioManager();
                    audioManager.openAudioConnection(channel);

                    messageChannel = event.getChannel();

                    isInit = true;

                    Config.getInstance().setMessageChannel(event.getChannel());
                } else {
                    event.getChannel().sendMessage("Такого канала не существует").queue();
                }
            }
        } else {
            event.getChannel().sendMessage("Бот уже инициализирован").queue();
        }
    }
}
