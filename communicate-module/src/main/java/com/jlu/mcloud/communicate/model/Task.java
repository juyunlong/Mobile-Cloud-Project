package com.jlu.mcloud.communicate.model;


import com.jlu.mcloud.communicate.interfaces.IProduct;

import java.io.Serializable;

/**
 * Created by koko on 17-3-20.
 */
public class Task implements IProduct, Serializable{
    private String taskId;
    private String taskFromNodeId;
    private String taskFileName;
    private boolean isDone;
    private int taskIndex = -1;
    private float rate = -1F;
    private String taskDataFileName;

    public int getTaskIndex() {
        return taskIndex;
    }

    public void setTaskIndex(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskFromNodeId() {
        return taskFromNodeId;
    }

    public void setTaskFromNodeId(String taskFromNodeId) {
        this.taskFromNodeId = taskFromNodeId;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public String getTaskFileName() {
        return taskFileName;
    }

    public void setTaskFileName(String taskFileName) {
        this.taskFileName = taskFileName;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public String getTaskDataFileName() {
        return taskDataFileName;
    }

    public void setTaskDataFileName(String taskDataFileName) {
        this.taskDataFileName = taskDataFileName;
    }
}
