package com.jlu.mcloud.communicate.heartbeat;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by koko on 2017/3/16.
 */
public class HeartbeatPacket implements Serializable {

    private long time;
    private String nodeId;
    private String error;
    private Map<String, Object> info = new HashMap<String, Object>();

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Map<String, Object> getInfo() {
        return info;
    }

    public void setInfo(Map<String, Object> info) {
        this.info = info;
    }
}
