package com.jlu.mcloud.communicate.fileio;

import com.jlu.mcloud.communicate.config.Config;
import com.jlu.mcloud.rpc.client.RPCClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by koko on 17-3-20.
 */
public class FileWriter {
    private String ip = Config.IP;
    private int port = Config.PORT;

    public FileWriter() {
    }

    public FileWriter(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void write(File file) throws InterruptedException {
        Thread writeThread = new Thread(new WriterTask(file));
        writeThread.start();
        writeThread.join();
    }

    private class WriterTask implements Runnable {
        private File file;

        WriterTask(File file) {
            this.file = file;
        }

        @Override
        public void run() {
            FileIoHandler handler = RPCClient.getRemoteProxyObject(
                    FileIoHandler.class,
                    new InetSocketAddress(ip, port)
            );

            FileEntity fileEntity = new FileEntity();
            fileEntity.setFileName(file.getName());
            fileEntity.setLength(file.length());
            fileEntity.setTime(System.currentTimeMillis());
            fileEntity.setNodeId(getNodeId());
            fileEntity.setErrorMsg("NO_ERROE");
            try {
                byte[] content = FileWriter.toArrayByte(file);
                fileEntity.setData(content);
            } catch (IOException e) {
                e.printStackTrace();
                fileEntity.setData("error file".getBytes());
            }

            try {
                handler.writeFile(fileEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected String getNodeId() {
        return "Default NodeId";
    }

    /**
     * 将指定文件转化成字节数组
     * @param file
     * @return
     * @throws IOException
     */
    public static byte[] toArrayByte(File file) throws IOException {
        FileChannel fileChannel = null;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            fileChannel = fileInputStream.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) fileChannel.size());
            while (fileChannel.read(byteBuffer) > 0) {
            }
            return byteBuffer.array();
        } catch (IOException e) {
            throw e;
        } finally {
            if (fileChannel != null) {
                fileChannel.close();
            }
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }
    }
}
