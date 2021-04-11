package ru.pshiblo;

import net.dv8tion.jda.api.entities.MessageChannel;

import java.io.File;
import java.io.IOException;

public class Config {

    private static final Config instance = new Config();

    public static Config getInstance() {
        return instance;
    }

    private long maxTimeTrack;
    private long timeInsert;
    private long timeList;
    private String videoId;
    private String liveChatId;
    private MessageChannel messageChannel;
    private String path;
    private boolean isDiscord;

    private Config() {
        maxTimeTrack = 3 * 60 * 1000;
        timeInsert = 5 * 60 * 1000;
        timeList = 20 * 1000;
        isDiscord = false;
        try {
            path = new File(".").getCanonicalPath();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long getMaxTimeTrack() {
        return maxTimeTrack;
    }

    public void setMaxTimeTrack(long maxTimeTrack) {
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

    public String getLiveChatId() {
        return liveChatId;
    }

    public void setLiveChatId(String liveChatId) {
        this.liveChatId = liveChatId;
    }

    public long getTimeInsert() {
        return timeInsert;
    }

    public void setTimeInsert(long timeInsert) {
        this.timeInsert = timeInsert;
    }

    public long getTimeList() {
        return timeList;
    }

    public void setTimeList(long timeList) {
        this.timeList = timeList;
    }

    public boolean isDiscord() {
        return isDiscord;
    }

    public void setDiscord(boolean discord) {
        isDiscord = discord;
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
