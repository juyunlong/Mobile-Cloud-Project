package com.jlu.mcloud.communicate.fileio;

import java.util.Map;

/**
 * Created by koko on 2017/5/19.
 */
public interface IFileTransHandler {
    /**
     * 上传通知接口
     *
     * @param localFileLength
     * @param remoteFileName  远程文件名
     * @return 返回值是 FileUploadTrandfer 的 key，用于识别FileUploadTransfer对象
     */
    public String startUploadFile(long localFileLength, String remoteFileName);

    public String startUploadFile(long localFileLength, String remoteFileName, Object metadata);


    /**
     * 上传过程接口
     * @param fileKey
     * @param content
     * @return
     */
    public boolean updateUploadProcess(String fileKey, byte[] content);

    /**
     * 下载通知接口
     * @param objectId 文件对象的ID，这个ID是MongoDB里面的ObjectId对象的Hex值
     * @return
     */
    public Map<String, String> startDownloadFile(String objectId);


    /**
     * 下载文件指定部分
     * @param objectId  文件对象的ID，这个ID是MongoDB里面的ObjectId对象的Hex值
     * @param offset    开始下载的偏移量
     * @param length    下载的字节长度
     * @return
     */
    public Map<String, String> startDownloadFile(String objectId, long offset, long length);

    /**
     * 下载过程接口
     * @param fileKey
     * @return
     */
    public byte[] updateDownloadProgress(String fileKey);
}
