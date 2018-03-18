package com.jlu.mcloud.manager;

import com.jlu.mcloud.config.Config;
import com.jlu.mcloud.util.MCLogger;
import com.jlu.mcloud.utils.Constant;
import com.jlu.mcloud.db.mongo.MongoManager;
import com.mongodb.Block;
import com.mongodb.client.MongoCollection;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by koko on 17-3-22.
 */
public class ConnectionManager {

    private volatile Map<String, AtomicBoolean> connectionMap = new HashMap<String, AtomicBoolean>();

    private ConnectionManager() {
        new Thread(new CheckConnectStatuTask()).start();
    }

    public static ConnectionManager getInstance() {
        return SingleHolder.INSTANCE;

    }

    private static class SingleHolder {
        public static final ConnectionManager INSTANCE = new ConnectionManager();
    }

    class CheckConnectStatuTask implements Runnable {
        @Override
        public void run() {
            MongoManager mongoManager = new MongoManager();
            MongoCollection<Document> collection = mongoManager.getDBCollection(Constant.TABLE_HEARTBEAT);
            Block<Document> checkBlock = new Block<Document>() {
                @Override
                public void apply(Document document) {
                    String deviceId = (String) document.get("_id");
                    Long lastLiveTime = (Long) document.get("timestamp");
                    //检测上次心跳与当前时间差，超过一分钟则视为离线
                    if ((System.currentTimeMillis() - lastLiveTime) > Config.DISCONNECT_TIME) {
                        connectionMap.put(deviceId, new AtomicBoolean(false));
                    } else {
                        MCLogger.info("在线的设备：" + deviceId);
                        connectionMap.put(deviceId, new AtomicBoolean(true));
                    }
                }
            };
            while (true) {
                collection.find().forEach(checkBlock);
                try {
                    Thread.sleep(Config.CHECK_TIME_INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void putConnectionSet(String nodeID) {
        connectionMap.put(nodeID, new AtomicBoolean(true));
    }

    public List<String> getAllOnlineNode() {
        List<String> ret = new ArrayList<String>();
        for (Map.Entry<String, AtomicBoolean> entry : connectionMap.entrySet()) {
            if (entry.getValue().get()) {
                ret.add(entry.getKey());
            }
        }
        return ret;
    }
}
