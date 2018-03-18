package com.jlu.mcloud.communicate.model;

import java.io.Serializable;

/**
 * Created by koko on 17-3-23.
 */
public class Respond implements Serializable {
    private String nodeId;
    private String taskId;
    private Object result;
    private long timestamp;

    public Respond(String nodeId, Object result, long timestamp) {
        this.nodeId = nodeId;
        this.result = result;
        this.timestamp = timestamp;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
