package com.jlu.mcloud.communicate.fileio;

import com.sun.org.apache.xpath.internal.operations.String;

/**
 * 用于设置上传时的部分参数选项
 * Created by koko on 2017/5/20.
 */
public class McloudFSUploadOptions {
    private Integer transferBufferSize = 1024 * 1024;
    private Object metadata = "nothing";

    public McloudFSUploadOptions() {
    }

    public Integer getTransferBufferSize() {
        return transferBufferSize;
    }

    public McloudFSUploadOptions transferBufferSize(final Integer bufferSize) {
        this.transferBufferSize = bufferSize;
        return this;
    }

    public Object getMetadata() {
        return metadata;
    }

    public McloudFSUploadOptions metadata(final Object metadata) {
        this.metadata = metadata;
        return this;
    }
}
