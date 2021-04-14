package ru.pshiblo.audio;

import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import com.sedmelluq.discord.lavaplayer.format.AudioPlayerInputStream;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import ru.pshiblo.base.ComponentThread;
import ru.pshiblo.gui.ConsoleOut;

import javax.sound.sampled.*;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;

import static com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats.COMMON_PCM_S16_BE;

public class LocalAudio extends ComponentThread {

    private static LocalAudio instance;

    public static LocalAudio getInstance() {
        if (instance == null) {
            throw new IllegalCallerException("LocalAudio not init (call LocalAudio.init();)");
        }
        return instance;
    }

    public static void init() {
        LocalAudio.instance = new LocalAudio();
        LocalAudio.instance.start();
    }

    private AudioPlayer player;
    private AudioPlayerManager playerManager;
    private Queue<String> tracks;

    private LocalAudio() {
        super();
        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
        playerManager.getConfiguration().setOutputFormat(COMMON_PCM_S16_BE);
        player = playerManager.createPlayer();
        tracks = new ArrayDeque<>();

        TrackScheduler trackScheduler = new TrackScheduler(tracks, playerManager);
        player.addListener(trackScheduler);
    }

    @Override
    protected void runInThread() {
        try {
            AudioDataFormat format = playerManager.getConfiguration().getOutputFormat();
            AudioInputStream stream = AudioPlayerInputStream.createStream(player, format, 10000L, false);
            SourceDataLine.Info info = new DataLine.Info(SourceDataLine.class, stream.getFormat());
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(stream.getFormat());
            line.start();

            byte[] buffer = new byte[COMMON_PCM_S16_BE.maximumChunkSize()];
            int chunkSize;

            while ((chunkSize = stream.read(buffer)) >= 0) {
                if (tracks.peek() != null) {
                    line.write(buffer, 0, chunkSize);
                }
            }
        }catch (LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }

    public void play(String track) {
        if (tracks.peek() == null) {
            ConsoleOut.println("Очередь пуста - запускаем трек");
            tracks.offer(track);
            playerManager.loadItem(track, new AudioLoadHandler(player, track));
        } else {
            ConsoleOut.println("Очередь не пуста - трек записан в очередь");
            tracks.offer(track);
            ConsoleOut.printList(tracks, "Очередь музыки");
        }
    }

    public void playNext() {
        if (tracks.peek() != null) {
            playerManager.loadItem(tracks.peek(), new AudioLoadHandler(player, tracks.peek()));
        }
    }

    public void playNextAndRemove(String trackRemove) {
        tracks.removeIf((item) -> item.equals(trackRemove));
        playNext();
    }

    public void setPlayer(AudioPlayer player) {
        this.player = player;
    }

    public AudioPlayer getPlayer() {
        return player;
    }

    public AudioPlayerManager getPlayerManager() {
        return playerManager;
    }

    public void setPlayerManager(AudioPlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    public Queue<String> getTracks() {
        return tracks;
    }

    public void setTracks(Queue<String> tracks) {
        this.tracks = tracks;
    }
}
