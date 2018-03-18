package com.jlu.mcloud.service;

import com.jlu.mcloud.communicate.fileio.IFileTransHandler;
import com.jlu.mcloud.fileio.FileDownloadTransfer;
import com.jlu.mcloud.fileio.FileUploadTransfer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by koko on 2017/5/19.
 */
public class FileTransHandlerImpl implements IFileTransHandler {

    private static ConcurrentHashMap<String, FileUploadTransfer> uploadFileMonitor = new ConcurrentHashMap<String, FileUploadTransfer>();
    private static ConcurrentHashMap<String,FileDownloadTransfer> downloadFileMonitor = new ConcurrentHashMap<String,FileDownloadTransfer>();

    @Override
    public String startUploadFile(long localFileLength, String remoteFileName) {
        FileUploadTransfer uploadTransfer = new FileUploadTransfer(localFileLength, remoteFileName);
        if (uploadFileMonitor.containsKey(uploadTransfer.getFileKey())) {
            uploadFileMonitor.remove(uploadTransfer.getFileKey());
        }
        uploadFileMonitor.put(uploadTransfer.getFileKey(), uploadTransfer);
        return uploadTransfer.getFileKey();
    }

    @Override
    public String startUploadFile(long localFileLength, String remoteFileName, Object metadata) {
        FileUploadTransfer uploadTransfer = new FileUploadTransfer(localFileLength, remoteFileName, metadata);
        if (uploadFileMonitor.containsKey(uploadTransfer.getFileKey())) {
            uploadFileMonitor.remove(uploadTransfer.getFileKey());
        }
        uploadFileMonitor.put(uploadTransfer.getFileKey(), uploadTransfer);
        return uploadTransfer.getFileKey();
    }

    @Override
    public boolean updateUploadProcess(String fileKey, byte[] content) {
        if (uploadFileMonitor.containsKey(fileKey)) {
            FileUploadTransfer transfer = uploadFileMonitor.get(fileKey);
            try {
                transfer.addContentBytes(content);
            } catch (Exception e) {
                return false;
            }
            if (transfer.isFileSaved()) {
                uploadFileMonitor.remove(fileKey);
            }
        } else {
            return false;
        }
        return true;
    }

    @Override
    public Map<String, String> startDownloadFile(String objecId) {
        FileDownloadTransfer downloadTransfer = new FileDownloadTransfer(objecId);
        if (downloadFileMonitor.containsKey(downloadTransfer.getFileKey())) {
            downloadFileMonitor.remove(downloadTransfer.getFileKey());
        }
        downloadFileMonitor.put(downloadTransfer.getFileKey(), downloadTransfer);
        Map<String, String> fileInfo = new HashMap<String, String>();
        fileInfo.put("fileLength", downloadTransfer.getFileLength() + "");
        fileInfo.put("fileKey", downloadTransfer.getFileKey());
        return fileInfo;
    }

    @Override
    public Map<String, String> startDownloadFile(String objectId, long offset, long length) {
        FileDownloadTransfer downloadTransfer = new FileDownloadTransfer(objectId, offset, length);
        if (downloadFileMonitor.containsKey(downloadTransfer.getFileKey())) {
            downloadFileMonitor.remove(downloadTransfer.getFileKey());
        }
        downloadFileMonitor.put(downloadTransfer.getFileKey(), downloadTransfer);
        Map<String, String> fileInfo = new HashMap<String, String>();
        fileInfo.put("fileLength", downloadTransfer.getFileLength() + "");
        fileInfo.put("fileKey", downloadTransfer.getFileKey());
        return fileInfo;
    }

    @Override
    public byte[] updateDownloadProgress(String fileKey) {
        if (downloadFileMonitor.containsKey(fileKey)) {
            FileDownloadTransfer transfer = downloadFileMonitor.get(fileKey);
            try {
                byte[] buffer = transfer.read();
                if (transfer.isReadFinish()) {
                    transfer.clossDownloadStream();
                }
                return buffer;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }
}
