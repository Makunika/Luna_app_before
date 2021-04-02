package ru.pshiblo.youtube;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.LiveChatMessage;
import com.google.api.services.youtube.model.LiveChatMessageSnippet;
import com.google.api.services.youtube.model.LiveChatTextMessageDetails;
import ru.pshiblo.Config;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class WorkerYouTubeLiveChatInsert implements Runnable {

    synchronized public static void insertMessage(String message) {
        try {
            System.out.println("Отправляем сообщение :" + message);
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
            System.out.println(request);
            LiveChatMessage response = request.execute();
            System.out.println(response);
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                insertMessage("У нас работает бот для музыки! Для этого введите /track <название трека/ссылка на музыку из youtube>");
                Thread.sleep(4 * 60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
