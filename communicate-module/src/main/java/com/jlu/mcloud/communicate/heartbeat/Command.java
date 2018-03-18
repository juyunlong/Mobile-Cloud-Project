package com.jlu.mcloud.communicate.heartbeat;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by koko on 2017/3/16.
 */
public class Command implements Serializable{
    private String nodeId;
    private String error;
    private Map<String, Object> info = new HashMap<String, Object>();

    public String getNodeID() {
        return nodeId;
    }

    public void setNodeID(String nodeID) {
        this.nodeId = nodeID;
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
