package com.dawdawich.utils

import com.dawdawich.bot.Bot
import org.telegram.telegrambots.exceptions.TelegramApiException

import java.util.ArrayList
import java.util.Calendar
import java.util.Random
import java.util.TimeZone

class EarmarkedPost(private val minInterval: Int, private val maxInterval: Int, private val photoQueue: PhotoQueue) : Runnable {

    private var adTimeInterval: Int = 0
    private var activeAd = false
    private var adInQueue = false
    private val telegramAds = ArrayList<TelegramAd>()

    private var time = 0
    private val r = Random()

    override fun run() {
        while (true) {
            val curTime = System.currentTimeMillis()
            if (adInQueue) {
                val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                val minute = calendar.get(Calendar.MINUTE)
                val hour = calendar.get(Calendar.HOUR_OF_DAY)

                telegramAds.stream()
                        .filter { a ->
                            var needHour = hour + a.timeZone
                            if (needHour > 24) {
                                needHour -= 24
                            }
                            minute == a.minute && needHour == a.hour && !a.isActive
                        }
                        .forEach { a ->
                            try {
                                a.adId = Bot.sendAd(a.newAd)
                                a.isActive = true
                                adTimeInterval = a.intervalTop
                            } catch (e: TelegramApiException) {
                                e.printStackTrace()
                            }

                            activeAd = true
                        }
                adInQueue = telegramAds.stream()
                        .anyMatch { a -> !a.isActive }
            }
            if (time > 0 || activeAd) {
                time--
                if (activeAd && adTimeInterval > 0) {
                    adTimeInterval--
                } else {
                    activeAd = false
                }
            } else {
                val photo = photoQueue.photo
                if (photo != null) {
                    try {
                        Bot.send(photo)
                    } catch (e: TelegramApiException) {
                        e.printStackTrace()
                    }
                }
                time = r.nextInt(maxInterval - minInterval) + minInterval
                println("Next post in $time minutes.\n")
            }
            telegramAds.stream()
                    .filter { it.isActive }
                    .forEach { a ->
                        if (a.interval > 0) {
                            a.interval = a.interval - 1
                        } else {
                            try {
                                Bot.deleteAd(a.adId)
                            } catch (e: TelegramApiException) {
                                e.printStackTrace()
                            }
                            telegramAds.remove(a)
                        }
                    }
            try {
                Thread.sleep(60000 - (System.currentTimeMillis() - curTime))
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    fun setAd(telegramAd: TelegramAd) {
        adInQueue = true
        telegramAds.add(telegramAd)
    }

}
