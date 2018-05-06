package com.dawdawich;

import com.dawdawich.bot.Bot;
import com.dawdawich.utils.EarmarkedPost;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        Bot bot = new Bot();
        try {
            telegramBotsApi.registerBot(bot);
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }

        EarmarkedPost earmarkedPost = new EarmarkedPost();
        Thread thread = new Thread(earmarkedPost);
        thread.setDaemon(true);
        thread.start();
    }
}
