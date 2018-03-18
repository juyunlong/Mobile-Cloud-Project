package com.jlu.mcloud.communicate.fileio;

import java.io.Serializable;

/**
 * Created by koko on 17-3-20.
 */
public class FileEntity implements Serializable {

    private String nodeId;
    private long time;
    private String fileName;
    private long length;
    private byte[] content;
    private String errorMsg;

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public byte[] getData() {
        return content;
    }

    public void setData(byte[] content) {
        this.content = content;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
