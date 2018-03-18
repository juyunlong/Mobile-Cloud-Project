package com.jlu.mcloud.db.mongo;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.bson.Document;

import java.util.Arrays;

/**
 * Created by koko on 17-3-18.
 */
public class MongoManager {

    private static MongoClientOptions.Builder builder;
    private static ServerAddress serverAddress;
    private MongoClient client;
    private MongoDatabase database;
    private GridFSBucket gridFSBucket;

    static {
        initialize();
    }

    public MongoManager(String dbName, String username, String passwd) {
        if (dbName == null || "".equals(dbName)) {
            throw new NumberFormatException("DBName is null");
        }

        if (MongoConfig.isAuthentication()) {
            MongoCredential credential = MongoCredential.createCredential(username, dbName, passwd.toCharArray());
            client = new MongoClient(serverAddress, Arrays.asList(credential), builder.build());
        } else {
            client = new MongoClient(serverAddress, builder.build());
        }
        database = client.getDatabase(dbName);
        gridFSBucket = GridFSBuckets.create(database, "datafiles");
    }

    public MongoManager() {
        this(MongoConfig.getDbName(), MongoConfig.getUsername(), MongoConfig.getPasswd());
    }

    public MongoCollection<Document> getDBCollection(String tableName) {
        return database.getCollection(tableName);
    }

    public void createCollection(String tableName) {
        database.createCollection(tableName);
    }

    public boolean isCollectionExist(String tableName) {
        MongoIterable<String> colliter = database.listCollectionNames();
        for (String collName :
                colliter) {
            if (tableName.equals(collName)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 初始化数据库链接参数
     */
    private static void initialize() {
        if (MongoConfig.getHost() == null || "".equals(MongoConfig.getHost())) {
            throw new NumberFormatException("host is null");
        }

        if (MongoConfig.getPort() == -1) {
            throw new NumberFormatException("port is null");
        }

        try {
            builder = MongoClientOptions.builder();
            builder.connectionsPerHost(MongoConfig.getConnectionsPerHost());
            builder.threadsAllowedToBlockForConnectionMultiplier(
                    MongoConfig.getThreadsAllowedBlockForConnectionMultiplier()
            );
            serverAddress = new ServerAddress(MongoConfig.getHost(), MongoConfig.getPort());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public GridFSBucket getGridFSBucket() {
        return gridFSBucket;
    }
}
