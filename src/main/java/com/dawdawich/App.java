package com.dawdawich;

import com.dawdawich.bot.Bot;
import com.dawdawich.config.Configuration;
import com.dawdawich.utils.EarmarkedPost;
import com.dawdawich.utils.PhotoQueue;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class App {
    public static void main(String[] args) throws IOException {
        ApiContextInitializer.init();

        File[] files = new File("./memeViewerProps").listFiles();
        ArrayList<Configuration> configs = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".properties")) {
                    configs.add(new Configuration(file));
                }
            }
        }

        for (Configuration conf : configs) {
            PhotoQueue photoQueue = new PhotoQueue();

            EarmarkedPost earmarkedPost = new EarmarkedPost(conf.getMinInterval(), conf.getMaxInterval(), photoQueue);
            Thread thread = new Thread(earmarkedPost);
            thread.setDaemon(true);
            thread.start();

            TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
            Bot bot = new Bot(conf, photoQueue);
            try {
                telegramBotsApi.registerBot(bot);
            } catch (TelegramApiRequestException e) {
                e.printStackTrace();
            }
        }
    }
}
