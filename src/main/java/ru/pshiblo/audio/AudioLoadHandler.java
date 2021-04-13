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

    private AudioPlayer player;
    private String track;
    private Queue<String> tracks;

    public AudioLoadHandler(AudioPlayer player, String track, Queue<String> tracks) {
        this.player = player;
        this.track = track;
        this.tracks = tracks;
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
        tracks.removeIf((item) -> item.equals(track));
        LocalAudio.playNext();
    }

    @Override
    public void loadFailed(FriendlyException e) {
        ConsoleOut.println("Ошибка при загрузке трека:  " + e.getMessage());
    }
}
