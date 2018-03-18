package com.jlu.mcloud.communicate.fileio;

import java.io.*;
import java.util.Map;

/**
 * McloudFSBucket的默认实现类
 * Created by koko on 2017/5/20.
 */
public class McloudFSBucketImpl implements McloudFSBucket {

    private IFileTransHandler fileTransHandler;
    private ProgressCallback callback = null;

    public McloudFSBucketImpl(IFileTransHandler handler) {
        this.fileTransHandler = handler;
    }

    @Override
    public boolean uploadFromStream(File file) {
        return uploadFromStream("DefaultData", file);
    }

    @Override
    public boolean uploadFromStream(String remoteFileName, File file) {
        try {
            long fileLength = file.length();
            FileInputStream inputStream = new FileInputStream(file);
            return uploadFromStream(remoteFileName, fileLength, inputStream, new McloudFSUploadOptions());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean uploadFromStream(String remoteFileName, String filePath) {
        return uploadFromStream(remoteFileName, filePath, new McloudFSUploadOptions());
    }

    @Override
    public boolean uploadFromStream(String remoteFileName, String filePath, McloudFSUploadOptions options) {
        try {
            File file = new File(filePath);
            long fileLength = file.length();
            FileInputStream inputStream = new FileInputStream(file);
            return uploadFromStream(remoteFileName, fileLength, inputStream, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean uploadFromStream(String remoteFileName, long fileLenth, InputStream source, McloudFSUploadOptions options) {
        String fileKey = fileTransHandler.startUploadFile(fileLenth, remoteFileName, options.getMetadata());
        if (fileKey == null) {
            return false;
        }
        int bufferSize = options.getTransferBufferSize();
        byte[] buffer = new byte[bufferSize];
        long offset = 0;
        int readByteSize;

        try {
            while ((readByteSize = source.read(buffer)) != -1) {
                offset += readByteSize;
                if (fileTransHandler.updateUploadProcess(fileKey, buffer)) {
                    if (callback != null) {
                        double finishPercent = (offset * 1.0 / fileLenth) * 100;
                        callback.onProgressChanged(finishPercent);
                    }
                } else {
                    throw new Exception("There is an Error in the peocess updateUploadProcess");
                }
            }
            if (offset != fileLenth) {
                throw new Exception("Can not upload the full file");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean downloadToStream(String objectId, String savePath) {
        Map<String, String> fileInfo = fileTransHandler.startDownloadFile(objectId);
        String fileKey = null;
        long fileLength = -1;
        if (fileInfo.containsKey("fileKey")) {
            fileKey = fileInfo.get("fileKey");
        }
        if (fileInfo.containsKey("fileLength")) {
            fileLength = Long.parseLong(fileInfo.get("fileLength"));
        }
        if (fileKey == null || fileLength <= 0) {
            return false;
        }
        boolean status;
        try {
            OutputStream destination = new FileOutputStream(savePath);
            status = download(fileKey, destination, fileLength);
        } catch (Exception e) {
            e.printStackTrace();
            status = false;
        }
        return status;
    }

    @Override
    public boolean downloadToStream(String objectId, OutputStream destination) {
        Map<String, String> fileInfo = fileTransHandler.startDownloadFile(objectId);
        String fileKey = null;
        long fileLength = -1;
        if (fileInfo.containsKey("fileKey")) {
            fileKey = fileInfo.get("fileKey");
        }
        if (fileInfo.containsKey("fileLength")) {
            fileLength = Long.parseLong(fileInfo.get("fileLength"));
        }
        if (fileKey == null || fileLength <= 0) {
            return false;
        }
        boolean status;
        try {
            status = download(fileKey, destination, fileLength);
        } catch (Exception e) {
            e.printStackTrace();
            status = false;
        }
        return status;
    }

    @Override
    public boolean downloadToStream(String objectId, String savePath, long offset, long length) {
        Map<String, String> fileInfo = fileTransHandler.startDownloadFile(objectId, offset, length);
        String fileKey = null;
        long fileLength = -1;
        if (fileInfo.containsKey("fileKey")) {
            fileKey = fileInfo.get("fileKey");
        }
        if (fileInfo.containsKey("fileLength")) {
            fileLength = Long.parseLong(fileInfo.get("fileLength"));
        }
        if (fileKey == null || fileLength <= 0) {
            return false;
        }
        boolean status;
        try {
            OutputStream destination = new FileOutputStream(savePath);
            status = download(fileKey, destination, length);
        } catch (Exception e) {
            e.printStackTrace();
            status = false;
        }
        return status;
    }

    @Override
    public boolean downloadToStream(String objectId, OutputStream destination, long offset, long length) {
        Map<String, String> fileInfo = fileTransHandler.startDownloadFile(objectId, offset, length);
        String fileKey = null;
        long fileLength = -1;
        if (fileInfo.containsKey("fileKey")) {
            fileKey = fileInfo.get("fileKey");
        }
        if (fileInfo.containsKey("fileLength")) {
            fileLength = Long.parseLong(fileInfo.get("fileLength"));
        }
        if (fileKey == null || fileLength <= 0) {
            return false;
        }
        boolean status;
        try {
            status = download(fileKey, destination, length);
        } catch (Exception e) {
            e.printStackTrace();
            status = false;
        }
        return status;
    }

    // 通用下载代码
    private boolean download(String fileKey, OutputStream destination, long length) throws Exception {
        if (fileKey == null || length <= -1) {
            return false;
        }
        long downloadedByteSize = 0;

        while (true) {
            if (downloadedByteSize >= length) {
                break;
            }
            byte[] buffer = fileTransHandler.updateDownloadProgress(fileKey);
            if (buffer == null) {
                throw new Exception("There is an Error in th process updateDownloadProgress");
            }
            if (downloadedByteSize + buffer.length > length) {
                int leftLen = (int) (length - downloadedByteSize);
                byte[] leftContent = new byte[leftLen];
                System.arraycopy(buffer, 0, leftContent, 0, leftLen);
                downloadedByteSize = length;
                destination.write(leftContent);
            } else {
                downloadedByteSize += buffer.length;
                destination.write(buffer);
            }
            destination.flush();
            if (callback != null) {
                double downloadPercent = (downloadedByteSize * 1.0 / length) * 100;
                callback.onProgressChanged(downloadPercent);
            }
        }
        if (downloadedByteSize != length) {
            throw new Exception("Can not download the full file");
        }
        return true;
    }


    @Override
    public void setCallBack(ProgressCallback callBack) {
        this.callback = callBack;
    }
}
