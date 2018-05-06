package com.dawdawich.utils;

import org.telegram.telegrambots.api.methods.PartialBotApiMethod;

import java.util.ArrayDeque;

public class PhotoQueue {

    private static PhotoQueue instance;
    private ArrayDeque<PartialBotApiMethod> photos = new ArrayDeque<>();

    private PhotoQueue() {

    }

    public static PhotoQueue getInstance() {
        if (instance == null)
            instance = new PhotoQueue();
        return instance;
    }

    public int addPhoto(PartialBotApiMethod photo) {
        photos.add(photo);
        return photos.size();
    }

    public PartialBotApiMethod getPhoto() {
        return photos.poll();
    }

    public int getSize() {
        return photos.size();
    }


}
