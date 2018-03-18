package com.jlu.mcloud.db.mongo;

import com.jlu.mcloud.config.Config;

/**
 * Created by koko on 17-3-18.
 */
public class MongoConfig {
    private static String username = null;
    private static String passwd = null;
    private static String host = Config.MONGO_HOST;
    private static int port = Config.MONGO_PORT;
    private static String dbName = Config.DBNAME;
    private static int connectionsPerHost = 20;
    private static int threadsAllowedBlockForConnectionMultiplier = 10; // 线程队列数
    private static boolean authentication = false; // 是否需要身份验证

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        MongoConfig.username = username;
    }

    public static String getPasswd() {
        return passwd;
    }

    public static void setPasswd(String passwd) {
        MongoConfig.passwd = passwd;
    }


    public static String getDbName() {
        return dbName;
    }

    public static void setDbName(String dbName) {
        MongoConfig.dbName = dbName;
    }

    public static int getConnectionsPerHost() {
        return connectionsPerHost;
    }

    public static void setConnectionsPerHost(int connectionsPerHost) {
        MongoConfig.connectionsPerHost = connectionsPerHost;
    }

    public static int getThreadsAllowedBlockForConnectionMultiplier() {
        return threadsAllowedBlockForConnectionMultiplier;
    }

    public static void setThreadsAllowedBlockForConnectionMultiplier(int count) {
        MongoConfig.threadsAllowedBlockForConnectionMultiplier = count;
    }

    public static boolean isAuthentication() {
        return authentication;
    }

    public static void setAuthentication(boolean authentication) {
        MongoConfig.authentication = authentication;
    }


    public static String getHost() {
        return host;
    }

    public static void setHost(String host) {
        MongoConfig.host = host;
    }

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        MongoConfig.port = port;
    }
}
