package com.jlu.mcloud.fileio;

import com.jlu.mcloud.config.Config;
import com.jlu.mcloud.db.mongo.MongoManager;
import com.mongodb.client.gridfs.GridFSUploadStream;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by koko on 2017/5/19.
 */
public class FileUploadTransfer implements Serializable {
    private static final long serialVersionUID = 1L;
    // 文件标识
    private String fileKey;
    // 客户端文件路径
    // 服务器上传目标文件路径
    // private String destFilePath;
    // 文件尺寸
    private long fileLength;
    // 已传输字节总数
    private long transferedByteCount;
    // 文件是否完成写入到服务器端磁盘
    private boolean isFileSaved;
    private GridFSUploadStream gridFSUploadStream;
    private ObjectId fileId;

    public FileUploadTransfer(long srcFileLength, String fileName) {
        fileKey = UUID.randomUUID().toString();
        this.fileLength = srcFileLength;
        this.isFileSaved = false;
        MongoManager manager = new MongoManager();
        GridFSUploadOptions options = new GridFSUploadOptions();
        options.chunkSizeBytes(Config.GRIDFS_FILE_CHUNK_SIZE);
        gridFSUploadStream = manager.getGridFSBucket().openUploadStream(fileName, options);
        fileId = gridFSUploadStream.getObjectId();
    }

    public FileUploadTransfer(long srcFileLength, String fileName, Object metaData) {
        fileKey = UUID.randomUUID().toString();
        this.fileLength = srcFileLength;
        this.isFileSaved = false;
        MongoManager manager = new MongoManager();
        GridFSUploadOptions options = new GridFSUploadOptions();
        options.chunkSizeBytes(Config.GRIDFS_FILE_CHUNK_SIZE);
        options.metadata(new Document("matadata", metaData));
        gridFSUploadStream = manager.getGridFSBucket().openUploadStream(fileName, options);
        fileId = gridFSUploadStream.getObjectId();
    }

    public void addContentBytes(byte[] content) {
        if (content == null || content.length == 0) {
            return;
        }
        // 如果之前已经传输的数据长度+本批数据长度>文件长度的话，说明这批数据是最后一批数据了；
        // 由于本批数据中可能会存在有空字节，所以需要筛选出来
        if (transferedByteCount + content.length > fileLength) {
            int leftByteSize = (int) (fileLength - transferedByteCount);
            byte[] leftContent = new byte[leftByteSize];
            System.arraycopy(content, 0, leftContent, 0, leftContent.length);
            transferedByteCount = fileLength;
            gridFSUploadStream.write(leftContent);
        } else { // 说明本批数据并非最后一批数据，文件还没有传输完。
            transferedByteCount += content.length;
            gridFSUploadStream.write(content);
        }

        if (transferedByteCount >= fileLength) {
            gridFSUploadStream.flush();
            isFileSaved = true;
            if (gridFSUploadStream != null) {
                gridFSUploadStream.close();
            }
        }
    }

    public String getFileKey() {
        return fileKey;
    }

    public long getFileLength() {
        return fileLength;
    }

    public long getTransferedByteCount() {
        return transferedByteCount;
    }

    public boolean isFileSaved() {
        return isFileSaved;
    }

    public ObjectId getFileId() {
        return fileId;
    }
}
