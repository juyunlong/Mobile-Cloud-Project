package com.jlu.mcloud.service;

import com.jlu.mcloud.communicate.config.Config;
import com.jlu.mcloud.communicate.heartbeat.Command;
import com.jlu.mcloud.communicate.heartbeat.HeartbeatHandler;
import com.jlu.mcloud.communicate.heartbeat.HeartbeatListener;
import com.jlu.mcloud.communicate.heartbeat.HeartbeatPacket;
import com.jlu.mcloud.manager.ConnectionManager;
import com.jlu.mcloud.utils.Constant;
import com.jlu.mcloud.db.mongo.MongoManager;
import com.jlu.mcloud.manager.TaskFactory;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.net.ConnectException;
import java.sql.Time;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by koko on 2017/3/16.
 */
public class HeartbeatHandlerImpl implements HeartbeatHandler {
    // 先实例化ConnectionManager 方便调试
    private ConnectionManager connectionManager = ConnectionManager.getInstance();

    @Override
    public Command sendHeartbeat(HeartbeatPacket packet) throws ConnectException {
        HeartbeatListener linstner = HeartbeatListener.getInstance();
        // 添加节点
        if (!linstner.checkNodeValid(packet.getNodeId())) {
            linstner.registerNode(packet.getNodeId(), packet);
        }

        //** 打印输出信息，没什么用，只是方便调试
        Map<String, Object> info = packet.getInfo();
        System.out.println("nodeid: " + packet.getNodeId());
        System.out.println("info: ");
        for (Map.Entry<String, Object> entry : info.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        // TODO: 将心跳包信息保存到 MongoDB 中
        insertPacketIntoMongo(packet);

        // TODO: 给客户端返回的命令（可以实现服务器向客户端发送指令或定时推送消息的功能）
        Command cmder = createCommand(packet.getNodeId());
        return cmder;
    }

    // 将心跳包存储到MongoDB里面
    private void insertPacketIntoMongo(HeartbeatPacket packet) {
        MongoManager mongoManager = new MongoManager();
        if (!mongoManager.isCollectionExist(Constant.TABLE_HEARTBEAT)) {
            mongoManager.createCollection(Constant.TABLE_HEARTBEAT);
        }
        MongoCollection<Document> collection = mongoManager.getDBCollection(Constant.TABLE_HEARTBEAT);

        Document document = new Document(packet.getInfo());
        document.append("_id", packet.getNodeId());
        document.append("timestamp", packet.getTime());

        FindIterable<Document> iterable = collection.find(Filters.eq("_id", packet.getNodeId()));
        if (iterable.iterator().hasNext()) {
            collection.updateOne(Filters.eq("_id", packet.getNodeId()), new Document("$set", document));
        } else {
            collection.insertOne(document);
        }
    }

    // 装配一个Command（心跳反回包）
    private Command createCommand(String nodeId) {
        Map<String, Object> retInfo = new HashMap<String, Object>();
        retInfo.put("task", TaskFactory.getInstance().createProduct(nodeId));
        Command cmder = new Command();
        cmder.setNodeID(nodeId);
        StringBuilder sbuilder = new StringBuilder("\n");
        List<String> nodes = connectionManager.getAllOnlineNode();
        for (String id : nodes) {
            sbuilder.append(id).append("\n");
        }
        cmder.setError("所有在线设备：" + nodes.size() + "台" + sbuilder);
        cmder.setInfo(retInfo);
        return cmder;
    }
}
