package com.jlu.mcloud.communicate.fileio;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by koko on 2017/5/20.
 */
public interface McloudFSBucket {

    /**
     * 默认的上传接口，所有参数均采用默认值，不建议使用
     *
     * @param file 需要上传的文件
     * @return 上传成功返回true，否则返回false
     */
    boolean uploadFromStream(File file);


    /**
     * @param remoteFileName 指定长传后的远程文件名
     * @param file           所要上传的本地文件
     * @return 上传成功返回true，否则返回false
     */
    boolean uploadFromStream(String remoteFileName, File file);


    /**
     * @param remoteFileName 指定长传后的远程文件名
     * @param filePath       所要上传的本地文件完整路径
     * @return 上传成功返回true，否则返回false
     */
    boolean uploadFromStream(String remoteFileName, String filePath);


    /**
     * @param remoteFileName 指定长传后的远程文件名
     * @param filePath       所要上传的本地文件完整路径
     * @param options        上传的选值，可以设置上传时的缓冲区大小 bufferSize 和附带的数据 metadata
     * @return 上传成功返回true，否则返回false
     */
    boolean uploadFromStream(String remoteFileName, String filePath, McloudFSUploadOptions options);


    /**
     * @param remoteFileName 指定长传后的远程文件名
     * @param fileLenth      所要上传的本地文件长度（字节数）
     * @param source         文件输入流
     * @param options        上传的选值，可以设置上传时的缓冲区大小 bufferSize 和附带的数据 metadata
     * @return 上传成功返回true，否则返回false
     */
    boolean uploadFromStream(String remoteFileName, long fileLenth, InputStream source, McloudFSUploadOptions options);


    /**
     * 默认下载接口，默认下载整个文件
     *
     * @param objectId MongoDB中与文件关联的id
     * @param savePath 本地存储的路径
     * @return 下载成功返回true，否则返回false
     */
    boolean downloadToStream(String objectId, String savePath);


    /**
     * 下载整个文件
     *
     * @param objectId    MongoDB中与文件关联的id
     * @param destination 目标输出流
     * @return 下载成功返回true，否则返回false
     */
    boolean downloadToStream(String objectId, OutputStream destination);


    /**
     * 下载文件的部分内容，从起始点下载制定长度的字节流
     *
     * @param objectId    MongoDB中与文件关联的id
     * @param destination 目标输出流
     * @param offset      开始下载的位置
     * @param length      下载的字节数
     * @return 下载成功返回true，否则返回false
     */
    boolean downloadToStream(String objectId, OutputStream destination, long offset, long length);


    /**
     * 下载文件的部分内容，从起始点下载制定长度的字节流
     *
     * @param objectId MongoDB中与文件关联的id
     * @param savePath 本地存储完整路径
     * @param offset   开始下载的位置
     * @param length   下载的字节数
     * @return 下载成功返回true，否则返回false
     */
    boolean downloadToStream(String objectId, String savePath, long offset, long length);


    /**
     * 设置回调接口
     * @param callBack
     */
    void setCallBack(ProgressCallback callBack);

    // 回调接口，用于获取传输进度
    public abstract class ProgressCallback {
        public abstract void onProgressChanged(double progress);
    }

}
