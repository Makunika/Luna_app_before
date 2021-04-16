package ru.pshiblo;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import ru.pshiblo.services.Context;

public class Utils {

    public static String getCurrentTrack() {
        if (Config.getInstance().isDiscord()) {
            return "";
        } else {
            AudioTrack playingTrack = Context.getLocalAudioService().getPlayer().getPlayingTrack();
            if (playingTrack != null) {
                return playingTrack.getInfo().title;
            } else {
                return "gavno";
            }
        }
    }

}
