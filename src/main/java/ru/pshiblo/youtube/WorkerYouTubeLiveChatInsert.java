package ru.pshiblo.youtube;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.LiveChatMessage;
import com.google.api.services.youtube.model.LiveChatMessageSnippet;
import com.google.api.services.youtube.model.LiveChatTextMessageDetails;
import ru.pshiblo.Config;
import ru.pshiblo.base.ComponentThread;
import ru.pshiblo.gui.ConsoleOut;

import java.io.IOException;
import java.util.List;

public class WorkerYouTubeLiveChatInsert extends ComponentThread {

    synchronized public static void insertMessage(String message) {
        try {
            ConsoleOut.println("Отправляем сообщение в чат: " + message);
            YouTube youtubeService = YouTubeInitializer.getYoutubeService();
            // Define the LiveChatMessage object, which will be uploaded as the request body.
            LiveChatMessage liveChatMessage = new LiveChatMessage();

            // Add the snippet object property to the LiveChatMessage object.
            LiveChatMessageSnippet snippet = new LiveChatMessageSnippet();
            snippet.setLiveChatId(Config.getInstance().getLiveChatId());
            LiveChatTextMessageDetails textMessageDetails = new LiveChatTextMessageDetails();
            textMessageDetails.setMessageText(message);
            snippet.setTextMessageDetails(textMessageDetails);
            snippet.setType("textMessageEvent");
            liveChatMessage.setSnippet(snippet);

            // Define and execute the API request
            YouTube.LiveChatMessages.Insert request = youtubeService.liveChatMessages()
                    .insert(List.of("snippet"), liveChatMessage);
            LiveChatMessage response = request.execute();
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
            ConsoleOut.alert(e.getMessage());
        }
    }

    public static void insertMessageAsync(String message) {
        new Thread(() -> insertMessage(message)).start();
    }

    @Override
    protected void runInThread() {
        while (true) {
            try {
                insertMessage("У нас работает бот для музыки! Для этого введите /track <название трека/ссылка на музыку из youtube>");
                Thread.sleep(Config.getInstance().getTimeInsert());
            } catch (InterruptedException e) {
                e.printStackTrace();
                ConsoleOut.alert(e.getMessage());
            }
        }
    }
}
