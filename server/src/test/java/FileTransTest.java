import com.jlu.mcloud.communicate.fileio.IFileTransHandler;
import com.jlu.mcloud.communicate.fileio.McloudFSBucket;
import com.jlu.mcloud.communicate.fileio.McloudFSBuckets;
import com.jlu.mcloud.communicate.fileio.McloudFSUploadOptions;
import com.jlu.mcloud.config.Config;
import com.jlu.mcloud.rpc.client.RPCClient;
import org.junit.Test;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.Map;

/**
 * Created by koko on 2017/5/19.
 */
public class FileTransTest {
    @Test
    public void upload() {
        IFileTransHandler handler = RPCClient.getRemoteProxyObject(IFileTransHandler.class,
                new InetSocketAddress("127.0.0.1", 8089));
        String localPath = "E:\\MCloud\\movie.rmvb";
        File file = new File(localPath);
        long len = file.length();
        String fileKey = handler.startUploadFile(len, "movie.rmvb");
        if (fileKey != null) {
            System.out.println(fileKey);
            byte[] buffer = new byte[Config.TRANSMISSION_BUFFER_SIZE];
            long offset = 0;
            int readByteSize = 0;
            try {
                InputStream inputStream = new FileInputStream(file);
                while ((readByteSize = inputStream.read(buffer)) != -1) {
                    offset += readByteSize;
                    handler.updateUploadProcess(fileKey, buffer);
                    double finishPercent = (offset * 1.0 / len) * 100;
                    System.out.println("finish percent: " + finishPercent);
                }
                if (offset != len) {
                    System.out.println("传输错误");
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void download() {
        IFileTransHandler handler = RPCClient.getRemoteProxyObject(IFileTransHandler.class,
                new InetSocketAddress("127.0.0.1", 8089));
        Map<String, String> info = handler.startDownloadFile("591f23d51485294b04e9d12f");
        String fileKey = info.get("fileKey");
        long lenth = Long.parseLong(info.get("fileLength"));
        System.out.println(info);
        long downloadedByteCount = 0;
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream("E:\\MCloud\\final.rmvb");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (fileKey != null && lenth != -1) {
            try {
                while (true) {
                    if (downloadedByteCount >= lenth) {
                        break;
                    }

                    byte[] buffer = handler.updateDownloadProgress(fileKey);
                    if (buffer == null) {
                        System.out.println("下载错误");
                        break;
                        //fc2bb08b000fe329dafa2e4c3ad05554
                        //e30099c2096b97d11047079ffc79bd7c
                    }
                    if (downloadedByteCount + buffer.length > lenth) {
                        int leftLen = (int) (lenth - downloadedByteCount);
                        byte[] leftContent = new byte[leftLen];
                        System.arraycopy(buffer, 0, leftContent, 0, leftContent.length);
                        downloadedByteCount = lenth;
                        assert outputStream != null;
                        outputStream.write(leftContent);
                    } else {
                        downloadedByteCount += buffer.length;
                        assert outputStream != null;
                        outputStream.write(buffer);
                    }
                    //outputStream.flush();
                    double downloadPercent = (downloadedByteCount * 1.0 / lenth) * 100;
                    System.out.println("download:" + downloadPercent + "%");
                }

                if (downloadedByteCount != lenth) {
                    System.out.println("文件大小不匹配");
                }
                outputStream.flush();
                outputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Test
    public void download2() {
        IFileTransHandler handler = RPCClient.getRemoteProxyObject(IFileTransHandler.class,
                new InetSocketAddress("127.0.0.1", 8089));
        Map<String, String> info = handler.startDownloadFile("591ef9da148529312070124e");
        String fileKey = info.get("fileKey");
        long lenth = Long.parseLong(info.get("fileLength"));
        System.out.println(info);
        long downloadedByteCount = 0;
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream("E:\\MCloud\\p.mp4");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void downloadPart() {
        IFileTransHandler handler = RPCClient.getRemoteProxyObject(IFileTransHandler.class,
                new InetSocketAddress("127.0.0.1", 8089));
        Map<String, String> info = handler.startDownloadFile("591fada4148529522c9541ed", 10, 10);
        String fileKey = info.get("fileKey");
        long fileLength = Long.parseLong(info.get("fileLength"));
        long lenth = 10;
        System.out.println(info);
        long downloadedByteCount = 0;
        String localPath = "E:\\MCloud\\part.txt";
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(localPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (fileKey != null && fileLength != -1) {
            try {

                while (true) {
                    if (downloadedByteCount >= lenth) {
                        break;
                    }

                    byte[] buffer = handler.updateDownloadProgress(fileKey);
                    if (buffer == null) {
                        System.out.println("下载错误");
                        break;
                    }
                    if (downloadedByteCount + buffer.length > lenth) {
                        int leftLen = (int) (lenth - downloadedByteCount);
                        byte[] leftContent = new byte[leftLen];
                        System.arraycopy(buffer, 0, leftContent, 0, leftContent.length);
                        downloadedByteCount = lenth;
                        outputStream.write(leftContent);
                    } else {
                        downloadedByteCount += buffer.length;
                        outputStream.write(buffer);
                    }
                    outputStream.flush();
                    double downloadPercent = (downloadedByteCount * 1.0 / 10) * 100;
                    System.out.println("download part: " + downloadPercent);
                }

                if (downloadedByteCount != lenth) {
                    System.out.println("文件大小不匹配");
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Test
    public void testDownloadPartToStream() {
        IFileTransHandler handler = RPCClient.getRemoteProxyObject(IFileTransHandler.class,
                new InetSocketAddress("127.0.0.1", 8089));
        McloudFSBucket mcloudFSBucket = McloudFSBuckets.create(handler);
        mcloudFSBucket.setCallBack(new McloudFSBucket.ProgressCallback() {
            @Override
            public void onProgressChanged(double progress) {
                System.out.println("finish: " + progress);
            }
        });

        mcloudFSBucket.downloadToStream("592003541485291e7c6351c4",
                "E:\\MCloud\\downloadPartToStream.txt", 10, 10);
    }

    @Test
    public void testDownloadPartToStream0() {
        IFileTransHandler handler = RPCClient.getRemoteProxyObject(IFileTransHandler.class,
                new InetSocketAddress("127.0.0.1", 8089));
        McloudFSBucket mcloudFSBucket = McloudFSBuckets.create(handler);
        mcloudFSBucket.setCallBack(new McloudFSBucket.ProgressCallback() {
            @Override
            public void onProgressChanged(double progress) {
                System.out.println("finish: " + progress);
            }
        });
        try {
            OutputStream outputStream = new FileOutputStream("E:\\MCloud\\downloadPartToStream2.txt");
            mcloudFSBucket.downloadToStream("592003541485291e7c6351c4",
                    outputStream, 10, 10);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testDownloadFullFile() {
        IFileTransHandler handler = RPCClient.getRemoteProxyObject(IFileTransHandler.class,
                new InetSocketAddress("127.0.0.1", 8089));
        McloudFSBucket mcloudFSBucket = McloudFSBuckets.create(handler);
        mcloudFSBucket.setCallBack(new McloudFSBucket.ProgressCallback() {
            @Override
            public void onProgressChanged(double progress) {
                System.out.println("finish: " + progress);
            }
        });
        mcloudFSBucket.downloadToStream("592007ad1485291e7c6351ca", "E:\\MCloud\\full.txt");
    }

    @Test
    public void testDownloadFullFile0() {
        IFileTransHandler handler = RPCClient.getRemoteProxyObject(IFileTransHandler.class,
                new InetSocketAddress("127.0.0.1", 8089));
        McloudFSBucket mcloudFSBucket = McloudFSBuckets.create(handler);
        mcloudFSBucket.setCallBack(new McloudFSBucket.ProgressCallback() {
            @Override
            public void onProgressChanged(double progress) {
                System.out.println("finish: " + progress);
            }
        });
        try {
            OutputStream outputStream = new FileOutputStream("E:\\MCloud\\full0.txt");
            mcloudFSBucket.downloadToStream("592007ad1485291e7c6351ca", outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testUploadToStream() {
        IFileTransHandler handler = RPCClient.getRemoteProxyObject(IFileTransHandler.class,
                new InetSocketAddress("127.0.0.1", 8089));
        McloudFSBucket mcloudFSBucket = McloudFSBuckets.create(handler);
        mcloudFSBucket.setCallBack(new McloudFSBucket.ProgressCallback() {
            @Override
            public void onProgressChanged(double progress) {
                System.out.println("upload: " + progress);
            }
        });
        mcloudFSBucket.uploadFromStream("upload.txt", "E:\\MCloud\\origin.txt");
    }

    @Test
    public void testUploadToStream0() {
        IFileTransHandler handler = RPCClient.getRemoteProxyObject(IFileTransHandler.class,
                new InetSocketAddress("127.0.0.1", 8089));
        McloudFSBucket mcloudFSBucket = McloudFSBuckets.create(handler);
        mcloudFSBucket.setCallBack(new McloudFSBucket.ProgressCallback() {
            @Override
            public void onProgressChanged(double progress) {
                System.out.println("upload: " + progress);
            }
        });
        try {
            File file = new File("E:\\MCloud\\origin.txt");
            InputStream inputStream = new FileInputStream(file);
            mcloudFSBucket.uploadFromStream("upload0.txt", file.length(), inputStream, new McloudFSUploadOptions());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testUploadToStreamWithMetadata() {
        IFileTransHandler handler = RPCClient.getRemoteProxyObject(IFileTransHandler.class,
                new InetSocketAddress("127.0.0.1", 8089));
        McloudFSBucket mcloudFSBucket = McloudFSBuckets.create(handler);
        mcloudFSBucket.setCallBack(new McloudFSBucket.ProgressCallback() {
            @Override
            public void onProgressChanged(double progress) {
                System.out.println("upload: " + progress);
            }
        });
        McloudFSUploadOptions options = new McloudFSUploadOptions()
                .transferBufferSize(1024 * 1024 * 10)
                .metadata("This is metadata");
        mcloudFSBucket.uploadFromStream("file_with_metadata.txt",
                "E:\\MCloud\\upload.txt", options);
    }


    @Test
    public void testUploadToStreamWithMetadata0() {
        IFileTransHandler handler = RPCClient.getRemoteProxyObject(IFileTransHandler.class,
                new InetSocketAddress("127.0.0.1", 8089));
        McloudFSBucket mcloudFSBucket = McloudFSBuckets.create(handler);
        mcloudFSBucket.setCallBack(new McloudFSBucket.ProgressCallback() {
            @Override
            public void onProgressChanged(double progress) {
                System.out.println("upload: " + progress);
            }
        });
        McloudFSUploadOptions options = new McloudFSUploadOptions()
                .transferBufferSize(1024 * 1024 * 10)
                .metadata("This is metadata");
        File file = new File("E:\\MCloud\\upload.txt");
        try {
            InputStream inputStream = new FileInputStream(file);
            mcloudFSBucket.uploadFromStream("file_with_metadata0.txt",
                    file.length(), inputStream, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
