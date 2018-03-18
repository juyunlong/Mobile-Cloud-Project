package com.jlu.mcloud.communicate.fileio;

import com.jlu.mcloud.rpc.client.RPCClient;

import java.net.InetSocketAddress;

/**
 * Created by koko on 2017/5/20.
 */
public final class McloudFSBuckets {

    /**
     * Create a new McloudFS bucket
     * @return
     */
    public static McloudFSBucket create(IFileTransHandler handler) {
        return new McloudFSBucketImpl(handler);
    }

    /**
     * 默认ip和端口
     * @return
     */
    public static McloudFSBucket create() {
        IFileTransHandler handler = RPCClient.getRemoteProxyObject(IFileTransHandler.class,
                new InetSocketAddress("127.0.0.1", 8089));
        return new McloudFSBucketImpl(handler);
    }

    /**
     * 提供ip和port的构造
     * @param ip 服务器的ip
     * @param port 服务器端口
     * @return the GridFSBucket
     */
    public static McloudFSBucket create(String ip, int port) {
        IFileTransHandler handler = RPCClient.getRemoteProxyObject(IFileTransHandler.class,
                new InetSocketAddress(ip, port));
        return new McloudFSBucketImpl(handler);
    }
}
