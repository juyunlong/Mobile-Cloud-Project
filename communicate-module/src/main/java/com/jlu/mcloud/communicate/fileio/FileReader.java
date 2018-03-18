package com.jlu.mcloud.communicate.fileio;

import com.jlu.mcloud.communicate.config.Config;
import com.jlu.mcloud.rpc.client.RPCClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by koko on 17-3-20.
 */
public class FileReader {
    private String ip = Config.IP;
    private int port = Config.PORT;

    public FileReader() {
    }

    public FileReader(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void read(String taskId, String storageDir) throws InterruptedException {
        ReadTask readTask = new ReadTask(taskId, storageDir);
        Thread readThread = new Thread(readTask);
        readThread.start();
        readThread.join();
    }

    private class ReadTask implements Runnable {
        private String taskId;
        private String storageDir;

        ReadTask(String taskId, String storageDir) {
            this.taskId = taskId;
            this.storageDir = storageDir;
        }

        @Override
        public void run() {
            FileIoHandler fileIoHandler = RPCClient.getRemoteProxyObject(
                    FileIoHandler.class, new InetSocketAddress(ip, port));
            try {
                FileEntity fileEntity = fileIoHandler.readFile(taskId);
                File file = new File(storageDir + fileEntity.getFileName());
                FileOutputStream outputStream = new FileOutputStream(file);
                outputStream.write(fileEntity.getData());
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
