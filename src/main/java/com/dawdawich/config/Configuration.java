package com.dawdawich.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Configuration {

    private String path;
    private String botId;
    private long chatId;
    private int minInterval;
    private int maxInterval;
    private List<Integer> ids = new ArrayList<>();

    public Configuration(File props) throws IOException {
        Properties properties = new Properties();
        try (InputStream is = new FileInputStream(props)) {
            properties.load(is);
        }
        this.path = properties.getProperty("path");
        String sIds = properties.getProperty("usersId");
        String sIntervals = properties.getProperty("interval");
        botId = properties.getProperty("botId");
        chatId = Long.parseLong(properties.getProperty("chatId"));
        String[] ids = sIds.split("_");
        for (String id : ids) {
            this.ids.add(Integer.parseInt(id));
        }
        String[] intervals = sIntervals.split("_");
        minInterval = Integer.parseInt(intervals[0]);
        maxInterval = Integer.parseInt(intervals[1]);
    }

    public String getPath() {
        return path;
    }

    public List<Integer> getIds() {
        return ids;
    }

    public String getBotId() {
        return botId;
    }

    public int getMinInterval() {
        return minInterval;
    }

    public int getMaxInterval() {
        return maxInterval;
    }

    public long getChatId() {
        return chatId;
    }

}
