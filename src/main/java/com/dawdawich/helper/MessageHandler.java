package com.dawdawich.helper;

import com.dawdawich.bot.Bot;
import com.dawdawich.utils.TelegramAd;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class MessageHandler {

    public static void handleAd(Message message, Bot bot, TelegramAd ad) throws TelegramApiException {
        if (message.hasPhoto()) {
            ad.setPhoto(message.getPhoto().get(message.getPhoto().size() - 1));
            sendAnswer(bot, "Photo set successfully", message.getChatId());
        }
        if (message.hasDocument()) {
            ad.setDocument(message.getDocument());
            sendAnswer(bot, "Document set successfully", message.getChatId());
        }
        if (message.hasText()) {
            String text = message.getText();
            if (text.startsWith("timezone")) {
                text = text.replaceAll("timezone:", "");
                ad.setTimeZone(Integer.parseInt(text));
                sendAnswer(bot, "Timezone set successfully", message.getChatId());
                return;
            }
            if (text.startsWith("timeout")) {
                text = text.replaceAll("timeout:", "");
                ad.setTimeout(Integer.parseInt(text));
                sendAnswer(bot, "Timeout set successfully", message.getChatId());
                return;
            }
            if (text.startsWith("hour")) {
                text = text.replaceAll("hour:", "");
                ad.setHour(Integer.parseInt(text));
                sendAnswer(bot, "Hour set successfully", message.getChatId());
                return;
            }
            if (text.startsWith("minute")) {
                text = text.replaceAll("minute:", "");
                ad.setMinute(Integer.parseInt(text));
                sendAnswer(bot, "Minute set successfully", message.getChatId());
                return;
            }
            if (text.startsWith("button")) {
                text = text.replaceAll("button:", "");
                String[] params = text.split("[|]");
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(params[0]);
                button.setUrl(params[1]);
                ad.getKeyboardButtons().add(button);
                sendAnswer(bot, "Button set successfully", message.getChatId());
                return;
            }

            ad.setDescription(message.getText());
            sendAnswer(bot, "Description set successfully", message.getChatId());
        }

    }

    private static void sendAnswer(Bot bot, String text, long id) throws TelegramApiException {
        SendMessage answer = new SendMessage();
        answer.setChatId(id);
        answer.setText(text);
        bot.execute(answer);
    }

}
