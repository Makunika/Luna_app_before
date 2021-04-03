package ru.pshiblo.youtube.listener;

import com.google.api.services.youtube.model.LiveChatMessage;

import java.util.List;

public interface YouTubeListenerList {
    void handle(List<LiveChatMessage> liveChatMessageList);
}
