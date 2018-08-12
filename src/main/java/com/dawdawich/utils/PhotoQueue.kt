package com.dawdawich.utils

import org.telegram.telegrambots.api.methods.PartialBotApiMethod

import java.util.ArrayDeque

class PhotoQueue {

    private val photos = ArrayDeque<PartialBotApiMethod<*>>()

    val photo: PartialBotApiMethod<*>?
        get() = photos.poll()

    val size: Int
        get() = photos.size

    fun addPhoto(photo: PartialBotApiMethod<*>): Int {
        photos.add(photo)
        return photos.size
    }
}
