package com.dawdawich.utils

import org.telegram.telegrambots.api.methods.PartialBotApiMethod
import org.telegram.telegrambots.api.methods.send.SendDocument
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.methods.send.SendPhoto
import org.telegram.telegrambots.api.objects.Document
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.api.objects.PhotoSize
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton

import java.util.ArrayList

class TelegramAd {

    var keyboardButtons: List<InlineKeyboardButton> = ArrayList()
        internal set
    private var description: String? = null
    var timeZone: Int = 0
    var hour: Int? = null
    var minute: Int? = null
    var intervalTop: Int = 0
    var interval: Int = 0
    private var photo: PhotoSize? = null
    private var document: Document? = null
    private var chatId: Long = 0
    var adId: Int = 0
    var isActive = false
    var newAd: PartialBotApiMethod<Message>? = null
        private set

    fun setDescription(description: String) {
        this.description = description
    }

    fun setPhoto(photo: PhotoSize) {
        this.photo = photo
    }

    fun setDocument(document: Document) {
        this.document = document
    }

    fun setChatId(chatId: Long) {
        this.chatId = chatId
    }

    fun earmarkedAd(earmarkedPost: EarmarkedPost) {
        val markup = InlineKeyboardMarkup()
        for (button in keyboardButtons) {
            markup.keyboard.add(object : ArrayList<InlineKeyboardButton>() {
                init {
                    add(button)
                }
            })
        }
        when {
            photo != null -> {
                newAd = SendPhoto()
                (newAd as SendPhoto).setChatId(chatId)
                (newAd as SendPhoto).photo = photo!!.fileId
                (newAd as SendPhoto).caption = description
                (newAd as SendPhoto).replyMarkup = markup
            }
            document != null -> {
                newAd = SendDocument()
                (newAd as SendDocument).setChatId(chatId)
                (newAd as SendDocument).document = document!!.fileId
                (newAd as SendDocument).caption = description
                (newAd as SendDocument).replyMarkup = markup
            }
            else -> {
                newAd = SendMessage()
                (newAd as SendMessage).setChatId(chatId)
                (newAd as SendMessage).text = description
                (newAd as SendMessage).replyMarkup = markup
            }
        }
        earmarkedPost.setAd(this)
    }

}

