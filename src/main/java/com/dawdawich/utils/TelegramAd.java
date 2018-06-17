package com.dawdawich.utils;

import com.dawdawich.bot.Bot;
import org.telegram.telegrambots.api.methods.BotApiMethod;
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

    private String description;
    private int timeZone;
    private Integer hour;
    private Integer minute;
    private int timeout;
    private PhotoSize photo;
    private Document document;
    private long chatId;
    List<InlineKeyboardButton> keyboardButtons = new ArrayList<>();

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

    public void setTimeout(int timeout) {
        this.timeout = timeout;
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

    public static void earmarkedAd(TelegramAd ad, Bot bot) {
        PartialBotApiMethod<Message> newAd;
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        for (InlineKeyboardButton button : ad.getKeyboardButtons()) {
            markup.getKeyboard().add(new ArrayList<InlineKeyboardButton>() {{
                add(button);
            }});
        }
        if (ad.photo != null) {
            newAd = new SendPhoto();
            ((SendPhoto) newAd).setChatId(ad.chatId);
            ((SendPhoto) newAd).setPhoto(ad.photo.getFileId());
            ((SendPhoto) newAd).setCaption(ad.description);
            ((SendPhoto) newAd).setReplyMarkup(markup);
        } else if (ad.document != null) {
            newAd = new SendDocument();
            ((SendDocument) newAd).setChatId(ad.chatId);
            ((SendDocument) newAd).setDocument(ad.document.getFileId());
            ((SendDocument) newAd).setCaption(ad.description);
            ((SendDocument) newAd).setReplyMarkup(markup);
        } else {
            newAd = new SendMessage();
            ((SendMessage) newAd).setChatId(ad.chatId);
            ((SendMessage) newAd).setText(ad.description);
            ((SendMessage) newAd).setReplyMarkup(markup);
        }



    }
}

