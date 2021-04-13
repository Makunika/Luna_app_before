package ru.pshiblo.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.managers.AudioManager;
import ru.pshiblo.Config;
import ru.pshiblo.gui.ConsoleOut;
import ru.pshiblo.youtube.WorkerYouTubeLiveChatInsert;

import java.io.IOException;
import java.util.Queue;

public class TrackScheduler extends AudioEventAdapter {

    private Queue<String> tracks;
    private AudioPlayerManager audioManager;

    public TrackScheduler(Queue<String> tracks, AudioPlayerManager audioManager) {
        this.tracks = tracks;
        this.audioManager = audioManager;
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        // Player was paused
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        // Player was resumed
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        try {
            ConsoleOut.println("Гугл хром замьютен, играет : " + track.getInfo().title);
            Runtime.getRuntime().exec(Config.getInstance().getPath() + "\\SoundVolumeView.exe /Mute Google");
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        tracks.poll();
        if (tracks.peek() != null) {
            audioManager.loadItem(tracks.peek(), new AudioLoadHandler(player, tracks.peek(), tracks));
        } else {
            try {
                ConsoleOut.println("Гугл хром размьютен");
                Runtime.getRuntime().exec(Config.getInstance().getPath() + "\\SoundVolumeView.exe /Unmute Google");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // endReason == FINISHED: A track finished or died by an exception (mayStartNext = true).
        // endReason == LOAD_FAILED: Loading of a track failed (mayStartNext = true).
        // endReason == STOPPED: The player was stopped.
        // endReason == REPLACED: Another track started playing while this had not finished
        // endReason == CLEANUP: Player hasn't been queried for a while, if you want you can put a
        //                       clone of this back to your queue
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        ConsoleOut.println("exp : " + exception.getMessage());
        // An already playing track threw an exception (track end event will still be received separately)
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        ConsoleOut.println("stuck");
        // Audio track has been unable to provide us any audio, might want to just start a new track
    }
}
