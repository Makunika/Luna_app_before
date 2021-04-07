package ru.pshiblo.youtube.listener.base;

import com.google.api.services.youtube.model.LiveChatMessage;

import java.util.List;

public interface YouTubeListenerList {
    void handle(List<LiveChatMessage> liveChatMessageList);
}