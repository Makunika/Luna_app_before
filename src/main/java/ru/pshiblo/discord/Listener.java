package ru.pshiblo.discord;

import com.google.api.client.util.DateTime;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;
import ru.pshiblo.Config;

import java.io.IOException;
import java.util.List;

public class Listener extends ListenerAdapter {

    private boolean play = false;
    private DateTime lastTime = new DateTime(10000);

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        Guild guild = event.getGuild();
        Message msg = event.getMessage();
        if (msg.getContentRaw().equals("!ping"))
        {
            MessageChannel channel = event.getChannel();
            channel.sendMessage("?play https://www.youtube.com/watch?v=3HrSVXP99kQ") /* => RestAction<Message> */
                    .queue();
        }
        else if(msg.getContentRaw().startsWith("!connect ")) {
            String arg = msg.getContentRaw().substring("!connect ".length());
            List<VoiceChannel> channels = guild.getVoiceChannelsByName(arg, true);
            if (!channels.isEmpty()) {
                VoiceChannel channel = channels.get(0);
                connectTo(channel);
                Config.getInstance().setMessageChannel(event.getChannel());
            }
            else {
                onMessage(event.getChannel(), "Такого канала не существует");
            }

        }
        else if(msg.getContentRaw().startsWith("!duration ")) {
            long duration = Long.parseLong(msg.getContentRaw().substring("!duration ".length())) + 500;
            if (duration < Config.getInstance().getMaxTimeTrack()) {
                play = true;
                new Thread(() -> {
                    try {
                        Runtime.getRuntime().exec(Config.getInstance().getPath() + "\\SoundVolumeView.exe /Mute Google");
                        Thread.sleep(duration);
                        Runtime.getRuntime().exec(Config.getInstance().getPath() + "\\SoundVolumeView.exe /Unmute Google");
                        play = false;
                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                event.getChannel().sendMessage("?skip").queue();
            }
        }
    }

    private void onMessage(MessageChannel channel, String message)
    {
        channel.sendMessage(message).queue();
    }

    private void connectTo(VoiceChannel channel)
    {
        Guild guild = channel.getGuild();
        AudioManager audioManager = guild.getAudioManager();
        audioManager.openAudioConnection(channel);
    }

    public boolean play(String track, DateTime publishedAt) {
        if (!play && publishedAt.getValue() > lastTime.getValue() && track.length() > 3) {
            lastTime = publishedAt;
            Config.getInstance().getMessageChannel().sendMessage("?play " + track).queue();
            return true;
        }
        return false;
    }
}
