package com.dawdawich.utils;

import org.telegram.telegrambots.api.methods.PartialBotApiMethod;

import java.util.ArrayDeque;

public class PhotoQueue {

    private ArrayDeque<PartialBotApiMethod> photos = new ArrayDeque<>();

    public PhotoQueue() {

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
