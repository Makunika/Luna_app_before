package ru.pshiblo.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import ru.pshiblo.gui.ConsoleOut;
import ru.pshiblo.youtube.WorkerYouTubeLiveChatInsert;

import java.util.Queue;

public class AudioLoadHandler implements AudioLoadResultHandler {

    private final AudioPlayer player;
    private final String track;

    public AudioLoadHandler(AudioPlayer player, String track) {
        this.player = player;
        this.track = track;
    }


    @Override
    public void trackLoaded(AudioTrack audioTrack) {
        ConsoleOut.println("Трек " + audioTrack.getInfo().title + " загружен");
        player.playTrack(audioTrack);
    }

    @Override
    public void playlistLoaded(AudioPlaylist audioPlaylist) {
        ConsoleOut.println("Треки " + audioPlaylist.getTracks().get(0).getInfo().title + " загружены");
        player.playTrack(audioPlaylist.getTracks().get(0));
    }

    @Override
    public void noMatches() {
        ConsoleOut.println("Трек " + track + " не найден");
        WorkerYouTubeLiveChatInsert.insertMessageAsync("Трек " + track + " не найден");
        LocalAudio.getInstance().playNextAndRemove(track);
    }

    @Override
    public void loadFailed(FriendlyException e) {
        ConsoleOut.println("Ошибка при загрузке трека:  " + e.getMessage());
    }
}
