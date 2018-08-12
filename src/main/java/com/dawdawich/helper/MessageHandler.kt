package com.dawdawich.helper

import com.dawdawich.bot.Bot
import com.dawdawich.utils.TelegramAd
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.exceptions.TelegramApiException

object MessageHandler {

    @Throws(TelegramApiException::class)
    fun handleAd(message: Message, bot: Bot, ad: TelegramAd) {
        if (message.hasPhoto()) {
            ad.setPhoto(message.photo[message.photo.size - 1])
            sendAnswer(bot, "Photo set successfully", message.chatId!!)
        }
        if (message.hasDocument()) {
            ad.setDocument(message.document)
            sendAnswer(bot, "Document set successfully", message.chatId!!)
        }
        if (message.hasText()) {
            var text = message.text
            when {
                text.startsWith("timezone") -> {
                    text = text.replace("timezone:".toRegex(), "")
                    ad.timeZone = Integer.parseInt(text)
                    sendAnswer(bot, "Timezone set successfully", message.chatId!!)
                    return
                }
                text.startsWith("intervals") -> {
                    text = text.replace("intervals:".toRegex(), "")
                    val intervals = text.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    ad.intervalTop = Integer.parseInt(intervals[0])
                    ad.interval = Integer.parseInt(intervals[1])
                    sendAnswer(bot, "intervals set successfully", message.chatId!!)
                    return
                }
                text.startsWith("hour") -> {
                    text = text.replace("hour:".toRegex(), "")
                    ad.hour = Integer.parseInt(text)
                    sendAnswer(bot, "Hour set successfully", message.chatId!!)
                    return
                }
                text.startsWith("minute") -> {
                    text = text.replace("minute:".toRegex(), "")
                    ad.minute = Integer.parseInt(text)
                    sendAnswer(bot, "Minute set successfully", message.chatId!!)
                    return
                }
                text.startsWith("button") -> {
                    text = text.replace("button:".toRegex(), "")
                    val params = text.split("[|]".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val button = InlineKeyboardButton()
                    button.text = params[0]
                    button.url = params[1]
                    button to ad.keyboardButtons
                    sendAnswer(bot, "Button set successfully", message.chatId!!)
                    return
                }
                else -> {
                    ad.setDescription(message.text)
                    sendAnswer(bot, "Description set successfully", message.chatId!!)
                }
            }

        }

    }

    @Throws(TelegramApiException::class)
    private fun sendAnswer(bot: Bot, text: String, id: Long) {
        val answer = SendMessage()
        answer.setChatId(id)
        answer.text = text
        bot.execute(answer)
    }

}
