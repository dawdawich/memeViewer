package com.dawdawich.utils;

import com.dawdawich.bot.Bot;
import org.telegram.telegrambots.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;

public class EarmarkedPost implements Runnable {

    static private int adTimeInterval;
    static private boolean activeAd = false;
    static private boolean adInQueue = false;
    static private ArrayList<TelegramAd> telegramAds = new ArrayList<>();

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
                int minute = calendar.get(Calendar.MINUTE);
                final int hour = calendar.get(Calendar.HOUR_OF_DAY);


                telegramAds.stream()
                        .filter(a -> {
                            int needHour = hour + a.getTimeZone();
                            if (needHour > 24) {
                                needHour -= 24;
                            }
                            return minute == a.getMinute() && needHour == a.getHour() && !a.isActive();
                        })
                        .forEach(a -> {
                            try {
                                a.setAdId(Bot.sendAd(a.getNewAd()));
                                a.setActive(true);
                                adTimeInterval = a.getIntervalTop();
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                            }
                            activeAd = true;
                        });

                adInQueue = telegramAds.stream()
                        .anyMatch(a -> !a.isActive());
            }
            if (time > 0 || activeAd) {
                time--;
                if (activeAd && adTimeInterval > 0) {
                    adTimeInterval--;
                } else {
                    activeAd = false;
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
            telegramAds.stream()
                    .filter(TelegramAd::isActive)
                    .forEach(a -> {
                        if (a.getInterval() > 0) {
                            a.setInterval(a.getInterval() - 1);
                        } else {
                            try {
                                Bot.deleteAd(a.getAdId());
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                            }
                            telegramAds.remove(a);
                        }
                    });
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void setAd(TelegramAd telegramAd) {
        adInQueue = true;
        telegramAds.add(telegramAd);
    }

}
