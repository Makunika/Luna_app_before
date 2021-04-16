package ru.pshiblo.services.audio.discord;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;
import ru.pshiblo.Config;
import ru.pshiblo.gui.log.ConsoleOut;
import ru.pshiblo.services.Context;
import ru.pshiblo.services.ServiceType;
import ru.pshiblo.services.youtube.ChatPostService;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class Listener extends ListenerAdapter {

    private boolean play;
    private final Queue<String> queue;

    public Listener() {
        queue = new ArrayDeque<>();
        play = false;
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        Guild guild = event.getGuild();
        Message msg = event.getMessage();
        if (msg.getContentRaw().equals("!ping"))
        {
            addToQueue("https://www.youtube.com/watch?v=3HrSVXP99kQ");
        }
        else if (msg.getContentRaw().startsWith("!connect ")) {
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
        else if (msg.getContentRaw().startsWith("!duration ")) {
            String[] split = msg.getContentRaw().substring("!duration ".length()).split("//");
            long duration = Long.parseLong(split[0]) + 500;
            String title = split[1];

            ConsoleOut.println("Сейчас играет: " + title);

            if (duration < Config.getInstance().getMaxTimeTrack()) {
                play = true;
                new Thread(() -> {
                    try {
                        final String currentThreadTrack = queue.peek().concat("");
                        Runtime.getRuntime().exec(Config.getInstance().getPath() + "\\SoundVolumeView.exe /Mute Google");
                        Thread.sleep(duration);
                        if (queue.size() <= 1) {
                            if (currentThreadTrack.equals(queue.peek())) {
                                Runtime.getRuntime().exec(Config.getInstance().getPath() + "\\SoundVolumeView.exe /Unmute Google");
                                queue.poll();
                            }
                        } else {
                            if (currentThreadTrack.equals(queue.peek())) {
                                queue.poll();
                                play(queue.peek());
                            }
                        }
                        play = false;
                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                queue.poll();
                event.getChannel().sendMessage("?skip").queue();
            }
        }
        else if (msg.getContentRaw().startsWith("!notfound ")) {
            queue.poll();
            String title = msg.getContentRaw().substring("!notfound ".length());
            ((ChatPostService) Context.getService(ServiceType.YOUTUBE_POST)).postMessage("Трек " + title + " не найден");
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

    public void addToQueue(String track) {
        Random random = new Random();
        if (queue.peek() == null) {
            queue.offer(track + "///" + random.nextLong());
            play(queue.peek());
        } else {
            queue.offer(track + "///" + random.nextLong());
        }
    }

    private void play(String track) {
        ConsoleOut.println("play : track = " + track);
        ConsoleOut.printList(queue, "Очередь музыки");
        Config.getInstance().getMessageChannel().sendMessage("?play " + track.split("///")[0]).queue();
    }

    public void stop() {
        ConsoleOut.println("Stop : play = " + play);
        ConsoleOut.printList(queue, "Очередь музыки");
        if (play) {
            Config.getInstance().getMessageChannel().sendMessage("?skip").queue();
            try {
                if (queue.size() <= 1) {
                    Runtime.getRuntime().exec(Config.getInstance().getPath() + "\\SoundVolumeView.exe /Unmute Google");
                    queue.poll();
                } else {
                    queue.poll();
                    play(queue.peek());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            play = false;
        }
    }


}
