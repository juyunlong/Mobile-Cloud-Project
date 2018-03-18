package com.jlu.mcloud.config;

import com.jlu.mcloud.util.MCLogger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by koko on 17-3-20.
 */
public class Config {
    public static String BASE_DIR;
    public static String MONGO_HOST;
    public static String DBNAME;
    public static int MONGO_PORT;
    public static int SERVER_PORT;
    public static int GRIDFS_FILE_CHUNK_SIZE;
    public static int TRANSMISSION_BUFFER_SIZE;
    public static final int DISCONNECT_TIME = 1000 * 20;
    public static final int CHECK_TIME_INTERVAL = 1000 * 5;

    // 从配置文件加载配置
    static {
        Properties properties = new Properties();
        InputStream is = Config.class.getClassLoader().getResourceAsStream("user.properties");
        try {
            properties.load(is);
            BASE_DIR = properties.getProperty("BASE_DIR", System.getProperty("user.dir")).trim();
            DBNAME = properties.getProperty("DBNAME", "mcloud").trim();
            MONGO_PORT = Integer.parseInt(properties.getProperty("MONGO_PORT", 27017 + ""));
            MONGO_HOST = properties.getProperty("MONGO_HOST", "127.0.0.1");
            SERVER_PORT = Integer.parseInt(properties.getProperty("SERVER_PORT", 8089 + ""));
            GRIDFS_FILE_CHUNK_SIZE = Integer.parseInt(properties.getProperty("GRIDFS_FILE_CHUNK_SIZE", 1024 * 1024 * 5 + ""));
            TRANSMISSION_BUFFER_SIZE = Integer.parseInt(properties.getProperty("TRANSMISSION_BUFFER_SIZE", 1024 * 1024 * 2 + ""));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            BASE_DIR = System.getProperty("user.dir");
            DBNAME = "mcloud";
            MONGO_HOST = "127.0.0.1";
            MONGO_PORT = 27017;
            SERVER_PORT = 8089;
            GRIDFS_FILE_CHUNK_SIZE = 1024 * 1024 * 5;
            TRANSMISSION_BUFFER_SIZE = 1024 * 1024 * 2;
            MCLogger.warning("You have not specify a propertiy file named \"user.properties\", All properties will use default value");
        }
    }

    // 用于服务器给上传的任务分配ID，这index是自增的
    private static volatile int taskIndex = 0;

    public synchronized static int getTaskIndex() {
        return taskIndex;
    }

    public synchronized static void incrementTaskIndex() {
        taskIndex++;
    }
}
