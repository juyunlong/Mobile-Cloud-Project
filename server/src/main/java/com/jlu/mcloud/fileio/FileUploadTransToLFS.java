package com.jlu.mcloud.fileio;

import java.io.*;
import java.util.UUID;

/**
 * Created by koko on 2017/5/19.
 */
public class FileUploadTransToLFS implements Serializable {
    private static final long serialVersionUID = 1L;
    // 文件标识
    private String fileKey;
    // 客户端文件路径
    private String srcFilePath;
    // 服务器上传目标文件路径
    private String destFilePath;
    // 文件尺寸
    private long fileLength;
    // 已传输字节总数
    private long transferedByteCount;
    // 文件是否完成写入到服务器端磁盘
    private boolean isFileSaved;
    private OutputStream outputStream;

    /**
     * @param srcFilePath 原文件路径
     * @param fileLength  原文件字节长度
     * @param destFilePath 目标文件路径
     */
    public FileUploadTransToLFS(String srcFilePath, long fileLength, String destFilePath) {
        this.fileKey = UUID.randomUUID().toString();
        this.srcFilePath = srcFilePath;
        this.fileLength = fileLength;
        this.destFilePath = destFilePath;
        this.isFileSaved = false;

        File localFile = new File(destFilePath);
        if (!localFile.getParentFile().exists()) {
            localFile.getParentFile().mkdirs();
        }
        try {
            this.outputStream = new FileOutputStream(localFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 往指定文件写入数据
     * @param content 要写入的字节数据
     * @throws IOException 可能抛出IO异常
     */
    public void addContentBytes(byte[] content) throws IOException {
        if (content == null || content.length == 0) {
            return;
        }
        //如果之前已经传输的数据长度+本批数据长度>文件长度的话，说明这批数据是最后一批数据了；
        //由于本批数据中可能会存在有空字节，所以需要筛选出来。
        if (transferedByteCount + content.length > fileLength) {
            int leftLength = (int) (fileLength - transferedByteCount);
            byte[] leftContent = new byte[leftLength];
            System.arraycopy(content, 0, leftContent, 0, leftContent.length);
            transferedByteCount = fileLength;
            outputStream.write(leftContent);
        } else {    // 说明不是最后一批数据，继续写入缓冲区
            transferedByteCount += content.length;
            outputStream.write(content);
        }
        if (transferedByteCount >= fileLength) {
            System.out.println("flush");
            outputStream.flush();
            isFileSaved = true;
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    public String getFileKey() {
        return fileKey;
    }

    public String getSrcFilePath() {
        return srcFilePath;
    }

    public String getDestFilePath() {
        return destFilePath;
    }

    public long getFileLength() {
        return fileLength;
    }

    public boolean isFileSaved() {
        return isFileSaved;
    }
}
