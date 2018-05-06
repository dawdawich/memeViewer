package com.dawdawich.helper;

import com.dawdawich.config.Configuration;
import org.telegram.telegrambots.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.objects.media.InputMedia;
import org.telegram.telegrambots.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class BotHelper {


    public static boolean checkUser (int id) throws IOException {
        for (Integer integer : Configuration.getInstance().getIds()) {
            if (integer == id) {
                return true;
            }
        }
        return false;
    }

    public static DeleteMessage createDeleteMessage(Long chatId, Integer messageId) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId.toString());
        deleteMessage.setMessageId(messageId);
        return deleteMessage;
    }

    public static SendPhoto createSendPhoto(File image, Long chatId) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setNewPhoto(image);
        sendPhoto.setChatId(chatId);
        return sendPhoto;
    }

    public static SendPhoto createSendPhoto(File image, Long chatId, InlineKeyboardMarkup markup) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setNewPhoto(image);
        sendPhoto.setChatId(chatId);
        sendPhoto.setReplyMarkup(markup);
        return sendPhoto;
    }


    public static SendPhoto createSendPhoto(String imageID, Long chatId) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setPhoto(imageID);
        sendPhoto.setChatId(chatId);
        return sendPhoto;
    }

    public static SendPhoto createSendPhoto(File image, Long chatId, String caption) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setNewPhoto(image);
        sendPhoto.setChatId(chatId);
        sendPhoto.setCaption(caption);
        return sendPhoto;
    }

    public static SendPhoto createSendPhoto(File image, Long chatId, String caption, InlineKeyboardMarkup markup) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setNewPhoto(image);
        sendPhoto.setChatId(chatId);
        sendPhoto.setCaption(caption);
        sendPhoto.setReplyMarkup(markup);
        return sendPhoto;
    }


    public static SendPhoto createSendPhoto(String imageID, Long chatId, String caption) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setPhoto(imageID);
        sendPhoto.setChatId(chatId);
        sendPhoto.setCaption(caption);
        return sendPhoto;
    }

    public static InlineKeyboardMarkup addLineMarkup(InlineKeyboardMarkup markup, HashMap<String, String> values) {
        if (markup.getKeyboard() == null) {
            markup.setKeyboard(new ArrayList<>());
        }
        List<InlineKeyboardButton> keyboardButtons = new ArrayList<>();
        for (Map.Entry<String, String> entry : values.entrySet()) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(entry.getKey());
            button.setCallbackData(entry.getValue());
            keyboardButtons.add(button);
        }
        markup.getKeyboard().add(keyboardButtons);
        return markup;
    }

    public static String getAttachText(File file) {
        String textName;
        if (file.isDirectory()) {
            textName = file.getAbsolutePath().concat(".txt");
        } else {
            textName = file.getAbsolutePath().replace("jpg", "txt");
        }
        if (Files.exists(Paths.get(textName))) {
            try {
                return Files.lines(Paths.get(textName)).reduce("", String::concat);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static List<InputMedia> setInputMedia(File[] photos) {
        List<InputMedia> photoList = new ArrayList<>();
        Arrays.stream(photos).forEach(f -> {
            InputMediaPhoto inputMediaPhoto = new InputMediaPhoto();
            inputMediaPhoto.setMedia(f, f.getAbsolutePath());
            photoList.add(inputMediaPhoto);
        });
        return photoList;
    }

    public static List<InputMedia> setInputMedia(List<String> ids) {
        List<InputMedia> photoList = new ArrayList<>();
        ids.forEach(s -> {
            InputMediaPhoto inputMediaPhoto = new InputMediaPhoto();
            inputMediaPhoto.setMedia(s);
            photoList.add(inputMediaPhoto);
        });
        return photoList;
    }

    public static SendMediaGroup crateMediaGroup(File file, Long chatId) {
        return crateMediaGroup(file, chatId, null);
    }

    public static SendMediaGroup crateMediaGroup(List<String> ids, Long chatId) {
        return crateMediaGroup(ids, chatId, null);
    }

    public static SendMediaGroup crateMediaGroup(File file, Long chatId, String caption) {
        File[] photos;
        if (file.listFiles() != null) {
            photos = Arrays
                    .stream(file.listFiles())
                    .sorted((f1, f2) -> {
                        String name1 = f1.getName();
                        String name2 = f2.getName();
                        name1 = name1.substring(0, name1.indexOf('.'));
                        name2 = name2.substring(0, name2.indexOf('.'));
                        int index1 = Integer.parseInt(name1);
                        int index2 = Integer.parseInt(name2);
                        return Integer.compare(index1, index2);
                    })
                    .toArray(File[]::new);
        } else {
            photos = new File[0];
        }
        SendMediaGroup sendMediaGroup = new SendMediaGroup();
        sendMediaGroup.setMedia(BotHelper.setInputMedia(photos));
        sendMediaGroup.setChatId(chatId);
        if (caption != null && !caption.isEmpty()) {
            sendMediaGroup.getMedia().get(0).setCaption(caption);
        }
        return sendMediaGroup;
    }

    public static SendMediaGroup crateMediaGroup(List<String> ids, Long chatId, String caption) {
        SendMediaGroup sendMediaGroup = new SendMediaGroup();
        sendMediaGroup.setMedia(BotHelper.setInputMedia(ids));
        sendMediaGroup.setChatId(chatId);
        if (caption != null && !caption.isEmpty()) {
            sendMediaGroup.getMedia().get(0).setCaption(caption);
        }
        return sendMediaGroup;
    }

    public static void deleteFile (String fileName) throws IOException {
        File file = new File(Configuration.getInstance().getPath() + File.separator + fileName);
        if (file.exists()) {
            if (file.isDirectory()) {
                File text = new File(file.getAbsolutePath() + ".txt");
                if (text.exists()) {
                    text.delete();
                }
                Files.walk(file.toPath())
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            } else {
                File text = new File(file.getAbsolutePath().replace("jpg", "txt"));
                if (text.exists()) {
                    text.delete();
                }
                file.delete();
            }
        }
    }

}
