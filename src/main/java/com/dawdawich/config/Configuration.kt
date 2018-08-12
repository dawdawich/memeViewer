package com.dawdawich.config

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.util.ArrayList
import java.util.Properties

class Configuration @Throws(IOException::class)
constructor(props: File) {

    val path: String
    val botId: String
    val chatId: Long
    val minInterval: Int
    val maxInterval: Int
    private val ids = ArrayList<Int>()

    init {
        val properties = Properties()
        FileInputStream(props).use { `is` -> properties.load(`is`) }
        this.path = properties.getProperty("path")
        val sIds = properties.getProperty("usersId")
        val sIntervals = properties.getProperty("interval")
        botId = properties.getProperty("botId")
        chatId = java.lang.Long.parseLong(properties.getProperty("chatId"))
        val ids = sIds.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (id in ids) {
            this.ids.add(Integer.parseInt(id))
        }
        val intervals = sIntervals.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        minInterval = Integer.parseInt(intervals[0])
        maxInterval = Integer.parseInt(intervals[1])
    }

    fun getIds(): List<Int> {
        return ids
    }

}
