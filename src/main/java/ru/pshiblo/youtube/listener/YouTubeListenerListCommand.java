package ru.pshiblo.youtube.listener;

import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.model.LiveChatMessage;
import ru.pshiblo.gui.ConsoleOut;

import java.util.Date;
import java.util.List;

public abstract class YouTubeListenerListCommand implements YouTubeListenerList {

    abstract protected String getCommand();

    abstract protected void handleCommand(String arg, LiveChatMessage liveChatMessage);

    private DateTime lastMessageTime = new DateTime(new Date());

    @Override
    public void handle(List<LiveChatMessage> liveChatMessageList) {

        for (int i = liveChatMessageList.size() - 1; i >= 0; i--) {
            LiveChatMessage liveChatMessage = liveChatMessageList.get(i);
            String messageText = liveChatMessage.getSnippet().getTextMessageDetails().getMessageText();
            if (messageText.matches(getCommand() + ".*") && liveChatMessage.getSnippet().getPublishedAt().getValue() > lastMessageTime.getValue()) {

                ConsoleOut.println("Новое сообщение от: "
                        + liveChatMessage.getAuthorDetails().getDisplayName()
                        + ". Команда: " + messageText
                        + ", Время: " + liveChatMessage.getSnippet().getPublishedAt());

                if (messageText.trim().length() == getCommand().length()) {
                    handleCommand("", liveChatMessage);
                } else {
                    handleCommand(messageText.trim().substring((getCommand() + " ").length()), liveChatMessage);
                }
                lastMessageTime = liveChatMessage.getSnippet().getPublishedAt();
                break;
            }
        }
    }



}
