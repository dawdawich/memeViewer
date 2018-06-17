package com.dawdawich.utils;

import com.dawdawich.bot.Bot;
import com.dawdawich.helper.BotHelper;
import org.telegram.telegrambots.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;

public class EarmarkedPost implements Runnable {

    static private int adTimeInterval;
    static private int adId;
    static private boolean activeAd = false;
    static private boolean adInQueue = false;
    static private int hour;
    static private int minute;
    static private int timezone;
    static private PartialBotApiMethod ad;

    private int time = 0;
    private Random r = new Random();
    private int minInterval;
    private int maxInterval;
    private PhotoQueue photoQueue;

    public EarmarkedPost(int minInterval, int maxInterval, PhotoQueue photoQueue) {
        this.minInterval = minInterval;
        this.maxInterval = maxInterval;
        this.photoQueue = photoQueue;
    }

    @Override
    public void run() {
        while (true) {
            if (adInQueue) {
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                int minute = calendar.getTime().getMinutes();
                int hour = calendar.getTime().getHours() + timezone;
                if (minute == EarmarkedPost.minute || hour = EarmarkedPost.hour) {
                    adId = Bot.sendAd(ad);
                    activeAd = true;
                    adInQueue = false;
                }
            }
            if (time > 0 || activeAd) {
                time--;
                if (activeAd && adTimeInterval > 0) {
                    adTimeInterval--;
                } else {
                    activeAd = false;
                    Bot.deleteAd(adId);
                }
            } else {
                PartialBotApiMethod photo = photoQueue.getPhoto();
                if (photo != null) {
                    try {
                        Bot.send(photo);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
                time = r.nextInt(maxInterval - minInterval) + minInterval;
                System.out.println("Next post in " + time + " minutes.\n");
            }
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void setAd(int timezone, int adTimeInterval, int hour, int minute, PartialBotApiMethod ad) {
        adInQueue = true;
        activeAd = false;
        EarmarkedPost.timezone = timezone;
        EarmarkedPost.adTimeInterval = adTimeInterval;
        EarmarkedPost.hour = hour;
        EarmarkedPost.minute = minute;
        EarmarkedPost.ad = ad;
    }

}
