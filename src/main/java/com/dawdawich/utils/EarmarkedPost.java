package com.dawdawich.utils;

import com.dawdawich.bot.Bot;
import com.dawdawich.config.Configuration;
import org.telegram.telegrambots.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.Calendar;
import java.util.Random;

public class EarmarkedPost implements Runnable {

    private int time = 0;
    private Random r = new Random();
    private int minInterval;
    private int maxInterval;

    public EarmarkedPost() throws IOException {
        minInterval = Configuration.getInstance().getMinInterval();
        maxInterval = Configuration.getInstance().getMaxInterval();
    }

    @Override
    public void run() {
        while (true) {
            if (time > 0) {
                time--;
            } else {
                PartialBotApiMethod photo = PhotoQueue.getInstance().getPhoto();
                if (photo != null) {
                    try {
                        Bot.send(photo);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
                if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 6) {
                    //TODO: add interval for night time
                    time = r.nextInt(maxInterval - minInterval) + minInterval;
                } else {
                    time = r.nextInt(maxInterval - minInterval) + minInterval;
                }
                System.out.println("Current hour: " + Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
                System.out.println("Current minute: " + Calendar.getInstance().get(Calendar.MINUTE));
                System.out.println("Next post in " + time + " minutes.\n");
            }
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}