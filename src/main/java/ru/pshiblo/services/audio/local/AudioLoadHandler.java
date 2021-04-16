package ru.pshiblo.services.audio.local;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import ru.pshiblo.gui.log.ConsoleOut;
import ru.pshiblo.services.Context;
import ru.pshiblo.services.ServiceType;
import ru.pshiblo.services.youtube.ChatPostService;

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
        ((ChatPostService) Context.getService(ServiceType.YOUTUBE_POST)).postMessage("Трек " + track + " не найден");
        Context.getLocalAudioService().playNextAndRemove(track);
    }

    @Override
    public void loadFailed(FriendlyException e) {
        ConsoleOut.println("Ошибка при загрузке трека:  " + e.getMessage());
    }
}
