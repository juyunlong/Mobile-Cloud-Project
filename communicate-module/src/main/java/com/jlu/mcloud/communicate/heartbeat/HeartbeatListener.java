package com.jlu.mcloud.communicate.heartbeat;

import com.jlu.mcloud.communicate.config.Config;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by koko on 2017/3/16.
 */
public class HeartbeatListener {

    private ExecutorService executor = Executors.newFixedThreadPool(Config.NTHREAD);

    private final ConcurrentHashMap<String, Object> nodes = new ConcurrentHashMap<String, Object>();
    private final ConcurrentHashMap<String, Long> nodeStatus = new ConcurrentHashMap<String, Long>();

    private long timeout = Config.TIMEOUT;

    private int port = Config.PORT;

    // 单例模式
    private static class SingleHolder {
        private static final HeartbeatListener INSTANCE = new HeartbeatListener();
    }

    private HeartbeatListener() {
    }

    public static HeartbeatListener getInstance() {
        return SingleHolder.INSTANCE;
    }

    public ConcurrentHashMap<String, Object> getNodes() {
        return nodes;
    }

    public void registerNode(String nodeId, Object nodeInfo) {
        nodes.put(nodeId, nodeInfo);
        nodeStatus.put(nodeId, System.currentTimeMillis());
    }

    public void removeNode(String nodeId) {
        if (nodes.contains(nodeId)) {
            nodes.remove(nodeId);
            // TODO : if the nodeStatus needs to remove?
        }
    }

    /**
     * 检测节点是否有效
     *
     * @param nodeId 节点的ID
     * @return 有效则返回true,
     */
    public boolean checkNodeValid(String nodeId) {
        if (!nodes.contains(nodeId) || !nodeStatus.contains(nodeId)) {
            return false;
        }
        if ((System.currentTimeMillis() - nodeStatus.get(nodeId)) > timeout) {
            return false;
        }
        return true;
    }


    /**
     * 删除所有实效节点
     */
    public void removeInvalidNode() {
        Iterator<Map.Entry<String, Long>> it = nodeStatus.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Long> e = it.next();
            //TODO : nodeStatus.get(e.getKey() ??
            if ((System.currentTimeMillis() - e.getValue()) > timeout) {
                nodeStatus.remove(e.getKey());
            }
        }
    }
}

