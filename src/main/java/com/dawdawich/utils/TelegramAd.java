package com.dawdawich.utils;

import org.telegram.telegrambots.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendDocument;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Document;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.PhotoSize;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class TelegramAd {

    List<InlineKeyboardButton> keyboardButtons = new ArrayList<>();
    private String description;
    private int timeZone;
    private Integer hour;
    private Integer minute;
    private int intervalTop;
    private int interval;
    private PhotoSize photo;
    private Document document;
    private long chatId;
    private int adId;
    private boolean isActive = false;
    private PartialBotApiMethod<Message> newAd;

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTimeZone(int timeZone) {
        this.timeZone = timeZone;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public void setMinute(Integer minute) {
        this.minute = minute;
    }

    public void setIntervalTop(int intervalTop) {
        this.intervalTop = intervalTop;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void setPhoto(PhotoSize photo) {
        this.photo = photo;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public List<InlineKeyboardButton> getKeyboardButtons() {
        return keyboardButtons;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public int getIntervalTop() {
        return intervalTop;
    }

    public int getInterval() {
        return interval;
    }

    public int getAdId() {
        return adId;
    }

    public void setAdId(int adId) {
        this.adId = adId;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getTimeZone() {
        return timeZone;
    }

    public Integer getHour() {
        return hour;
    }

    public Integer getMinute() {
        return minute;
    }

    public PartialBotApiMethod<Message> getNewAd() {
        return newAd;
    }

    public void earmarkedAd() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        for (InlineKeyboardButton button : keyboardButtons) {
            markup.getKeyboard().add(new ArrayList<InlineKeyboardButton>() {{
                add(button);
            }});
        }
        if (photo != null) {
            newAd = new SendPhoto();
            ((SendPhoto) newAd).setChatId(chatId);
            ((SendPhoto) newAd).setPhoto(photo.getFileId());
            ((SendPhoto) newAd).setCaption(description);
            ((SendPhoto) newAd).setReplyMarkup(markup);
        } else if (document != null) {
            newAd = new SendDocument();
            ((SendDocument) newAd).setChatId(chatId);
            ((SendDocument) newAd).setDocument(document.getFileId());
            ((SendDocument) newAd).setCaption(description);
            ((SendDocument) newAd).setReplyMarkup(markup);
        } else {
            newAd = new SendMessage();
            ((SendMessage) newAd).setChatId(chatId);
            ((SendMessage) newAd).setText(description);
            ((SendMessage) newAd).setReplyMarkup(markup);
        }
        EarmarkedPost.setAd(this);
    }

}

