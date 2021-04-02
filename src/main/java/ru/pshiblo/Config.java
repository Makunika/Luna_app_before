package ru.pshiblo;

import net.dv8tion.jda.api.entities.MessageChannel;

import java.io.File;
import java.io.IOException;

public class Config {

    private static final Config instance = new Config();

    public static Config getInstance() {
        return instance;
    }

    private int maxTimeTrack;
    private String videoId;
    private MessageChannel messageChannel;
    private String path;

    private Config() {
        maxTimeTrack = 3 * 60 * 1000;
        try {
            path = new File(".").getCanonicalPath();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getMaxTimeTrack() {
        return maxTimeTrack;
    }

    public void setMaxTimeTrack(int maxTimeTrack) {
        this.maxTimeTrack = maxTimeTrack;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public MessageChannel getMessageChannel() {
        return messageChannel;
    }

    public void setMessageChannel(MessageChannel messageChannel) {
        this.messageChannel = messageChannel;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "Config{" +
                "maxTimeTrack=" + maxTimeTrack +
                ", videoId='" + videoId + '\'' +
                ", messageChannel=" + messageChannel +
                ", path='" + path + '\'' +
                '}';
    }
}
