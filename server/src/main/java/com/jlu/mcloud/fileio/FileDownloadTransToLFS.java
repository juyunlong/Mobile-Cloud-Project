package com.jlu.mcloud.fileio;

import com.jlu.mcloud.config.Config;

import java.io.*;
import java.util.UUID;

/**
 * Created by koko on 2017/5/19.
 */
public class FileDownloadTransToLFS implements Serializable {
    private static final long serialVersionUID = 1L;
    private String fileKey;
    private InputStream inputStream = null;
    private long fileLength = 0;
    private String srcFilePath;
    private boolean isReadFinish = false;
    private long transferedByteCount = 0;

    public FileDownloadTransToLFS(String srcFilePath) {
        this.fileKey = UUID.randomUUID().toString();
        this.srcFilePath = srcFilePath;
        File file = new File(srcFilePath);
        this.fileLength = file.exists() ? file.length() : -1;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.isReadFinish = false;
    }

    // 一块一块的读取
    public byte[] read() throws IOException {
        if (inputStream == null) {
            return null;
        }
        byte[] buffer = new byte[Config.TRANSMISSION_BUFFER_SIZE];
        inputStream.read(buffer);
//        gridFSDownloadStream.mark();
        transferedByteCount += buffer.length;
        if (transferedByteCount >= fileLength) {
            isReadFinish = true;
            transferedByteCount = fileLength;
        }
        return buffer;
    }

    public void clossInputStream() throws IOException {
        if (inputStream != null) {
            inputStream.close();
        }
    }

    public String getFileKey() {
        return fileKey;
    }

    public long getFileLength() {
        return fileLength;
    }

    public boolean isReadFinish() {
        return isReadFinish;
    }

    public long getTransferedByteCount() {
        return transferedByteCount;
    }

}
