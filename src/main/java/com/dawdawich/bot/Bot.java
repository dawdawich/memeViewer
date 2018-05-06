package com.dawdawich.bot;

import com.dawdawich.config.Configuration;
import com.dawdawich.helper.BotHelper;
import com.dawdawich.utils.PhotoQueue;
import org.telegram.telegrambots.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Bot extends TelegramLongPollingBot {

    public static Bot instance;

    private static HashMap<String, List<String>> mediaGroup = new HashMap<>();

    private Long chatId = Configuration.getInstance().getChatId();

    public Bot() throws IOException {
    }

    @Override
    public String getBotToken() {
        instance = this;
        try {
            return Configuration.getInstance().getBotId();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onClosing() {

    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            Message message = update.getMessage();
            CallbackQuery callbackQuery = update.getCallbackQuery();
            if ((message != null && BotHelper.checkUser(message.getFrom().getId())) || (callbackQuery != null && BotHelper.checkUser(callbackQuery.getFrom().getId()))) {
                try {
                    callbackHandler(callbackQuery);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                messageHandler(message);
            }
        } catch (TelegramApiException | IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String getBotUsername() {
        return "WebFoeMyself Bot";
    }

    private void getPhotos(Message chatId) throws IOException {
        File memeFolder = new File(Configuration.getInstance().getPath() + File.separator);
        File[] memes = memeFolder.listFiles();
        if (memes != null) {
            Arrays.stream(memes)
                    .filter(f -> !f.getName().contains("txt"))
                    .limit(5)
                    .forEach(file -> {
                        String attachedText = BotHelper.getAttachText(file);
                        if (file.isDirectory()) {
                            List<Message> messages;
                            try {
                                if (attachedText != null && !attachedText.isEmpty()) {
                                    messages = sendMediaGroup(BotHelper.crateMediaGroup(file, chatId.getChatId(), attachedText));
                                } else {
                                    messages = sendMediaGroup(BotHelper.crateMediaGroup(file, chatId.getChatId()));
                                }
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                                return;
                            }
                            mediaGroup.put(file.getName(), messages.stream().map(m -> m.getPhoto().get(0).getFileId()).collect(Collectors.toList()));
                            SendMessage markup = new SendMessage();
                            markup.setReplyMarkup(getPostDeleteMarkup(messages, file.getName()));
                            markup.setChatId(chatId.getChatId());
                            markup.setText("↑");
                            try {
                                execute(markup);
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                            }
                            return;
                        }
                        try {
                            if (attachedText != null && !attachedText.isEmpty()) {
                                sendPhoto(BotHelper.createSendPhoto(file, chatId.getChatId(), attachedText, getPostDeleteMarkup(file.getName())));
                            } else {
                                sendPhoto(BotHelper.createSendPhoto(file, chatId.getChatId(), getPostDeleteMarkup(file.getName())));
                            }
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    });
        }
//        DeleteMessage deleteMessage = new DeleteMessage();
//        deleteMessage.setChatId(chatId.getChatId().toString());
//        deleteMessage.setMessageId(chatId.getMessageId());
//        execute(deleteMessage);

    }

    private InlineKeyboardMarkup getPostDeleteMarkup(String fileName) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        HashMap<String, String> firstLine = new HashMap<>();
        HashMap<String, String> secondLine = new HashMap<>();
        HashMap<String, String> thirdLine = new HashMap<>();
        firstLine.put("post", "p:" + fileName);
        firstLine.put("earmarked", "e:" + fileName);
        secondLine.put("without text", "w:" + fileName);
        secondLine.put("e without text", "t:" + fileName);
        thirdLine.put("delete", "d:" + fileName);
        keyboardMarkup = BotHelper.addLineMarkup(keyboardMarkup, firstLine);
        keyboardMarkup = BotHelper.addLineMarkup(keyboardMarkup, secondLine);
        keyboardMarkup = BotHelper.addLineMarkup(keyboardMarkup, thirdLine);
        return keyboardMarkup;
    }

    private InlineKeyboardMarkup getPostDeleteMarkup(List<Message> messages, String folderName) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        HashMap<String, String> firstLine = new HashMap<>();
        HashMap<String, String> secondLine = new HashMap<>();
        HashMap<String, String> thirdLine = new HashMap<>();
        String formedPost = folderName + "|" + String.join(",", messages.stream().map(f -> f.getMessageId().toString()).collect(Collectors.toList()));
        firstLine.put("post", "!:" + formedPost);
        firstLine.put("earmarked", "?:" + formedPost);
        secondLine.put("without text", "#:" + formedPost);
        secondLine.put("e without text", "$:" + formedPost);
        thirdLine.put("delete", "@:" + formedPost);
        keyboardMarkup = BotHelper.addLineMarkup(keyboardMarkup, firstLine);
        keyboardMarkup = BotHelper.addLineMarkup(keyboardMarkup, secondLine);
        keyboardMarkup = BotHelper.addLineMarkup(keyboardMarkup, thirdLine);

        return keyboardMarkup;
    }

    private void messageHandler(Message message) throws TelegramApiException, IOException {
        if (message != null) {
            switch (message.getText()) {
                case "/start":
                    SendMessage sendMessage = new SendMessage().setChatId(message.getChatId());
                    sendMessage.setText("Hello " + message.getFrom().getFirstName());
                    ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
                    KeyboardRow row = new KeyboardRow();
                    row.add(0, "Get memes");
                    row.add(1, "Queue size");
                    List<KeyboardRow> list = new ArrayList<>();
                    list.add(row);
                    markup.setKeyboard(list);
                    markup.setResizeKeyboard(true);
                    sendMessage.setReplyMarkup(markup);
                    execute(sendMessage);
                    break;
                case "Get memes":
                    getPhotos(message);
                    break;
                case "Queue size":
                    getQueueSize(message);
                    break;
            }
        }
    }

    private void getQueueSize(Message message) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Memes in queue: " + PhotoQueue.getInstance().getSize());
        sendMessage.setChatId(message.getChatId());
        execute(sendMessage);
    }

    private void callbackHandler(CallbackQuery callbackQuery) throws TelegramApiException, IOException {
        if (callbackQuery != null) {
            String data = callbackQuery.getData();
            switch (data.charAt(0)) {
                //post
                case 'p':
                    postPhoto(data.substring(data.indexOf(':') + 1, data.length()), callbackQuery.getMessage(), true);
                    break;
                //delete
                case 'd':
                    deletePhoto(data.substring(data.indexOf(':') + 1, data.length()), callbackQuery.getMessage());
                    break;
                //earmarked
                case 'e':
                    earmarkedPhoto(data.substring(data.indexOf(':') + 1, data.length()), callbackQuery.getMessage(), true);
                    break;
                //without text
                case 'w':
                    postPhoto(data.substring(data.indexOf(':') + 1, data.length()), callbackQuery.getMessage(), false);
                    break;
                //earmarked without text
                case 't':
                    earmarkedPhoto(data.substring(data.indexOf(':') + 1, data.length()), callbackQuery.getMessage(), false);
                    break;
                //post many photos
                case '!':
                    postPhotos(data.substring(data.indexOf(':') + 1, data.length()), callbackQuery.getMessage(), true);
                    break;
                //delete many photos
                case '@':
                    deletePhotos(data.substring(data.indexOf(':') + 1, data.length()), callbackQuery.getMessage());
                    break;
                //earmarked many photos
                case '?':
                    earmarkedPhotos(data.substring(data.indexOf(':') + 1, data.length()), callbackQuery.getMessage(), true);
                    break;
                //post many photos without text
                case '#':
                    postPhotos(data.substring(data.indexOf(':') + 1, data.length()), callbackQuery.getMessage(), false);
                    break;
                //earmarked many photos without text
                case '$':
                    earmarkedPhotos(data.substring(data.indexOf(':') + 1, data.length()), callbackQuery.getMessage(), false);
                    break;
            }
        }
    }

    private void postPhoto(String query, Message message, boolean withText) throws TelegramApiException, IOException {
        String[] queryParams = query.split("[|]");
        BotHelper.deleteFile(queryParams[0]);
        if (withText) {
            sendPhoto(BotHelper.createSendPhoto(message.getPhoto().get(0).getFileId(), chatId, message.getCaption()));
        } else {
            sendPhoto(BotHelper.createSendPhoto(message.getPhoto().get(0).getFileId(), chatId));
        }
        execute(BotHelper.createDeleteMessage(message.getChatId(), message.getMessageId()));
    }

    private void postPhotos(String query, Message message, boolean withText) throws IOException, TelegramApiException {
        String[] parseQuery = query.split("[|]");
        File folder = new File(Configuration.getInstance().getPath() + File.separator + parseQuery[0]);
        File textFile = new File(folder.getAbsolutePath() + ".txt");
        String[] messagesIds = parseQuery[1].split(",");

        if (withText && textFile.exists()) {
            sendMediaGroup(BotHelper.crateMediaGroup(folder, chatId, BotHelper.getAttachText(textFile)));
        } else {
            sendMediaGroup(BotHelper.crateMediaGroup(folder, chatId));
        }
        Arrays.stream(messagesIds).forEach(s -> {
            try {
                execute(BotHelper.createDeleteMessage(message.getChatId(), Integer.parseInt(s)));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        });
        BotHelper.deleteFile(folder.getName());
        execute(BotHelper.createDeleteMessage(message.getChatId(), message.getMessageId()));
    }

    private void deletePhoto(String fileName, Message message) throws TelegramApiException, IOException {
        BotHelper.deleteFile(fileName);
        execute(BotHelper.createDeleteMessage(message.getChatId(), message.getMessageId()));
    }

    private void deletePhotos(String query, Message message) throws IOException, TelegramApiException {
        String[] parseQuery = query.split("[|]");
        File folder = new File(Configuration.getInstance().getPath() + File.separator + parseQuery[0]);
        String[] messagesIds = parseQuery[1].split(",");
        Arrays.stream(messagesIds).forEach(s -> {
            try {
                execute(BotHelper.createDeleteMessage(message.getChatId(), Integer.parseInt(s)));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        });
        BotHelper.deleteFile(folder.getName());
        execute(BotHelper.createDeleteMessage(message.getChatId(), message.getMessageId()));
    }


    private void earmarkedPhoto(String query, Message message, boolean withText) throws TelegramApiException, IOException {
        String[] queryParams = query.split("[|]");
        int queue;
        if (withText) {
            queue = PhotoQueue.getInstance().addPhoto(BotHelper.createSendPhoto(message.getPhoto().get(0).getFileId(), chatId, message.getCaption()));
        } else {
            queue = PhotoQueue.getInstance().addPhoto(BotHelper.createSendPhoto(message.getPhoto().get(0).getFileId(), chatId));
        }
        BotHelper.deleteFile(queryParams[0]);
        execute(BotHelper.createDeleteMessage(message.getChatId(), message.getMessageId()));
        SendMessage queueSize = new SendMessage();
        queueSize.setChatId(message.getChatId());
        queueSize.setText("В очереди сейчас " + queue);
        execute(queueSize);
    }


    private void earmarkedPhotos(String query, Message message, boolean withText) throws TelegramApiException, IOException {
        String[] parseQuery = query.split("[|]");
        File folder = new File(Configuration.getInstance().getPath() + File.separator + parseQuery[0]);
        File textFile = new File(folder.getAbsolutePath() + ".txt");
        String[] messagesIds = parseQuery[1].split(",");
        int queue;
        if (withText && textFile.exists()) {
            queue = PhotoQueue.getInstance().addPhoto(BotHelper.crateMediaGroup(mediaGroup.get(parseQuery[0]), chatId, BotHelper.getAttachText(textFile)));
        } else {
            queue = PhotoQueue.getInstance().addPhoto(BotHelper.crateMediaGroup(mediaGroup.get(parseQuery[0]), chatId));
        }
        Arrays.stream(messagesIds).forEach(s -> {
            try {
                execute(BotHelper.createDeleteMessage(message.getChatId(), Integer.parseInt(s)));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        });
        mediaGroup.remove(parseQuery[0]);
        BotHelper.deleteFile(folder.getName());
        execute(BotHelper.createDeleteMessage(message.getChatId(), message.getMessageId()));
        SendMessage queueSize = new SendMessage();
        queueSize.setChatId(message.getChatId());
        queueSize.setText("В очереди сейчас " + queue);
        execute(queueSize);
    }

    public static void send(PartialBotApiMethod photo) throws TelegramApiException {
        if (photo instanceof SendPhoto) {
            instance.sendPhoto((SendPhoto) photo);
        } else if (photo instanceof SendMediaGroup) {
            instance.sendMediaGroup((SendMediaGroup) photo);
        }
    }
}