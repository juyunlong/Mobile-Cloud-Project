package com.jlu.mcloud.fileio;

import com.jlu.mcloud.db.mongo.MongoManager;
import com.mongodb.MongoGridFSException;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by koko on 2017/5/19.
 */
public class FileDownloadTransfer implements Serializable {
    private static final long serialVersionUID = 1L;
    private String fileKey;
    private GridFSDownloadStream gridFSDownloadStream = null;
    private long fileLength = 0;
    private ObjectId objectId;
    private boolean isReadFinish = false;
    private long transferedByteCount = 0;
    private int chunkSize = 0;
    private long offset = 0;
    private long downloadLength = 0;
    private boolean isSkip = false;

    public FileDownloadTransfer(String objectId) {
        this.fileKey = UUID.randomUUID().toString();
        this.objectId = new ObjectId(objectId);
        MongoManager manager = new MongoManager();
        try {
            this.gridFSDownloadStream = manager.getGridFSBucket().openDownloadStream(this.objectId);
            this.fileLength = gridFSDownloadStream.getGridFSFile().getLength();
            this.chunkSize = gridFSDownloadStream.getGridFSFile().getChunkSize();
        } catch (MongoGridFSException e) {
            e.printStackTrace();
            this.fileLength = -1;
        }
        this.offset = 0;
        this.downloadLength = fileLength;
        this.isReadFinish = false;
        this.isSkip = false;
    }

    public FileDownloadTransfer(String objectId, long offset, long downloadLength) {
        this(objectId);
        this.offset = offset;
        this.downloadLength = downloadLength;
    }

    // 一块一块的读取
    public byte[] read() {
        if (gridFSDownloadStream == null) {
            return null;
        }
        // 必须按照chunk的大小来读取。不然读取的数据会跟原数据不匹配，不明原因
        if (!isSkip) {
            long skipSize = 0;
            while (skipSize < offset) {
                gridFSDownloadStream.read();
                skipSize++;
            }

            gridFSDownloadStream.mark();
            gridFSDownloadStream.reset();
            isSkip = true;
        }

        byte[] buffer = new byte[chunkSize];

        gridFSDownloadStream.read(buffer);
        transferedByteCount += buffer.length;
        if (transferedByteCount >= downloadLength) {
            isReadFinish = true;
            transferedByteCount = downloadLength;
        }
        return buffer;
    }

    public byte[] readAll() {
        if (gridFSDownloadStream == null) {
            return null;
        }
        // 必须按照chunk的大小来读取。不然读取的数据会跟原数据不匹配，不明原因

        byte[] buffer = new byte[chunkSize];
        gridFSDownloadStream.read(buffer);
        transferedByteCount += buffer.length;
        if (transferedByteCount >= fileLength) {
            isReadFinish = true;
            transferedByteCount = fileLength;
        }
        return buffer;
    }

    public void clossDownloadStream() {
        if (gridFSDownloadStream != null) {
            gridFSDownloadStream.close();
        }
    }

    public String getFileKey() {
        return fileKey;
    }

    public long getFileLength() {
        return fileLength;
    }

    public ObjectId getObjectId() {
        return objectId;
    }

    public boolean isReadFinish() {
        return isReadFinish;
    }

    public long getTransferedByteCount() {
        return transferedByteCount;
    }
}
