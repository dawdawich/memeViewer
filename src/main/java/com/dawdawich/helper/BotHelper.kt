package com.dawdawich.helper

import org.telegram.telegrambots.api.methods.send.SendMediaGroup
import org.telegram.telegrambots.api.methods.send.SendPhoto
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage
import org.telegram.telegrambots.api.objects.media.InputMedia
import org.telegram.telegrambots.api.objects.media.InputMediaPhoto
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton

import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

object BotHelper {

    fun checkUser(id: Int, ids: List<Int>): Boolean {
        for (integer in ids) {
            if (integer == id) {
                return true
            }
        }
        return false
    }

    fun createDeleteMessage(chatId: Long, messageId: Int?): DeleteMessage {
        val deleteMessage = DeleteMessage()
        deleteMessage.chatId = chatId.toString()
        deleteMessage.messageId = messageId
        return deleteMessage
    }

    fun createSendPhoto(image: File, chatId: Long?): SendPhoto {
        val sendPhoto = SendPhoto()
        sendPhoto.setNewPhoto(image)
        sendPhoto.setChatId(chatId!!)
        return sendPhoto
    }

    fun createSendPhoto(image: File, chatId: Long?, markup: InlineKeyboardMarkup): SendPhoto {
        val sendPhoto = SendPhoto()
        sendPhoto.setNewPhoto(image)
        sendPhoto.setChatId(chatId!!)
        sendPhoto.replyMarkup = markup
        return sendPhoto
    }


    fun createSendPhoto(imageID: String, chatId: Long?): SendPhoto {
        val sendPhoto = SendPhoto()
        sendPhoto.photo = imageID
        sendPhoto.setChatId(chatId!!)
        return sendPhoto
    }

    fun createSendPhoto(image: File, chatId: Long?, caption: String): SendPhoto {
        val sendPhoto = SendPhoto()
        sendPhoto.setNewPhoto(image)
        sendPhoto.setChatId(chatId!!)
        sendPhoto.caption = caption
        return sendPhoto
    }

    fun createSendPhoto(image: File, chatId: Long?, caption: String, markup: InlineKeyboardMarkup): SendPhoto {
        val sendPhoto = SendPhoto()
        sendPhoto.setNewPhoto(image)
        sendPhoto.setChatId(chatId!!)
        sendPhoto.caption = caption
        sendPhoto.replyMarkup = markup
        return sendPhoto
    }


    fun createSendPhoto(imageID: String, chatId: Long?, caption: String): SendPhoto {
        val sendPhoto = SendPhoto()
        sendPhoto.photo = imageID
        sendPhoto.setChatId(chatId!!)
        sendPhoto.caption = caption
        return sendPhoto
    }

    fun addLineMarkup(markup: InlineKeyboardMarkup, values: HashMap<String, String>): InlineKeyboardMarkup {
        if (markup.keyboard == null) {
            markup.keyboard = ArrayList()
        }
        val keyboardButtons = ArrayList<InlineKeyboardButton>()
        for ((key, value) in values) {
            val button = InlineKeyboardButton()
            button.text = key
            button.callbackData = value
            keyboardButtons.add(button)
        }
        markup.keyboard.add(keyboardButtons)
        return markup
    }

    fun getAttachText(file: File): String? {
        val textName: String = when {
            file.isDirectory -> file.absolutePath + ".txt"
            else -> file.absolutePath.replace("jpg", "txt")
        }
        if (Files.exists(Paths.get(textName))) {
            try {
                return Files.lines(Paths.get(textName)).reduce("") { obj, str -> obj + str }
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return null
    }

    private fun setInputMedia(photos: Array<File?>): List<InputMedia<*>> {
        val photoList = ArrayList<InputMedia<*>>()
        Arrays.stream(photos).forEach { f ->
            val inputMediaPhoto = InputMediaPhoto()
            inputMediaPhoto.setMedia(f, f?.absolutePath)
            photoList.add(inputMediaPhoto)
        }
        return photoList
    }

    private fun setInputMedia(ids: List<String>): List<InputMedia<*>> {
        val photoList = ArrayList<InputMedia<*>>()
        ids.forEach { s ->
            val inputMediaPhoto = InputMediaPhoto()
            inputMediaPhoto.media = s
            photoList.add(inputMediaPhoto)
        }
        return photoList
    }

    @JvmOverloads
    fun crateMediaGroup(file: File, chatId: Long?, caption: String? = null): SendMediaGroup {
        val photos: Array<File?>
        if (file.listFiles() != null) {
            photos = Arrays
                    .stream(file.listFiles()!!)
                    .sorted { f1, f2 ->
                        var name1 = f1.name
                        var name2 = f2.name
                        name1 = name1.substring(0, name1.indexOf('.'))
                        name2 = name2.substring(0, name2.indexOf('.'))
                        val index1 = Integer.parseInt(name1)
                        val index2 = Integer.parseInt(name2)
                        Integer.compare(index1, index2)
                    }
                    .toArray { s -> arrayOfNulls<File>(s) }
        } else {
            photos = arrayOfNulls(0)
        }
        val sendMediaGroup = SendMediaGroup()
        sendMediaGroup.media = BotHelper.setInputMedia(photos)
        sendMediaGroup.setChatId(chatId!!)
        if (caption != null && !caption.isEmpty()) {
            sendMediaGroup.media[0].caption = caption
        }
        return sendMediaGroup
    }

    @JvmOverloads
    fun crateMediaGroup(ids: List<String>, chatId: Long?, caption: String? = null): SendMediaGroup {
        val sendMediaGroup = SendMediaGroup()
        sendMediaGroup.media = BotHelper.setInputMedia(ids)
        sendMediaGroup.setChatId(chatId!!)
        if (caption != null && !caption.isEmpty()) {
            sendMediaGroup.media[0].caption = caption
        }
        return sendMediaGroup
    }

    @Throws(IOException::class)
    fun deleteFile(fileName: String, path: String) {
        val file = File(path + File.separator + fileName)
        if (file.exists()) {
            if (file.isDirectory) {
                val text = File(file.absolutePath + ".txt")
                if (text.exists()) {
                    text.delete()
                }
                Files.walk(file.toPath())
                        .sorted(Comparator.reverseOrder())
                        .map<File> { it.toFile() }
                        .forEach { it.delete() }
            } else {
                val text = File(file.absolutePath.replace("jpg", "txt"))
                if (text.exists()) {
                    text.delete()
                }
                file.delete()
            }
        }
    }

}
