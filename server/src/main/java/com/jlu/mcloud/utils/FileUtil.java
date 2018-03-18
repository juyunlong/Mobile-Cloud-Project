package com.jlu.mcloud.utils;

import java.io.*;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by koko on 17-3-30.
 */
public class FileUtil {

    // TODO: 待修改
    public static String currentWorkDir = System.getProperty("user.dir");

    /**
     * 左填充
     *
     * @param str    源字符串
     * @param length 填充的长度
     * @param ch     填充的字符
     * @return 填充后的字符串
     */
    public static String leftPad(String str, int length, char ch) {
        if (str.length() >= length) {
            return str;
        }
        char[] chas = new char[length];
        Arrays.fill(chas, ch);
        char[] src = str.toCharArray();
        System.arraycopy(src, 0, chas, length - src.length, src.length);
        return new String(chas);
    }

    /**
     * 文件分割
     * @param fileName 等待分割的文件名
     * @param byteSize 分割的字节数
     * @return 分割后的文件列表
     * @throws IOException
     */
    public static List<String> splitBySize(String fileName, int byteSize) throws IOException {
        List<String> parts = new ArrayList<String>();
        File file = new File(fileName);
        int blockCount = (int) Math.ceil(file.length() / (double) byteSize);
        int countLen = (blockCount + "").length();
        // 用线程池来做这些事情
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
                blockCount,
                blockCount * 3,
                1, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(blockCount * 2)
        );
        for (int i = 0; i < blockCount; ++i) {
            String partFileName = file.getName() + "." + leftPad((i + 1) + "", countLen, '0') + ".part";
            threadPool.execute(new SplitTask(byteSize, partFileName, file, i * byteSize));
            parts.add(partFileName);
        }
        threadPool.shutdown();
        while (!threadPool.isTerminated());
        return parts;
    }



    private static class SplitTask implements Runnable {
        int byteSize;       // 块大小
        String partFileName;
        File originFile;    // 源文件
        int startPos;       // 开始吃位置

        SplitTask(int byteSize, String partFileName, File originFile, int startPos) {
            this.byteSize = byteSize;
            this.partFileName = partFileName;
            this.originFile = originFile;
            this.startPos = startPos;
        }

        @Override
        public void run() {
            RandomAccessFile rFile;
            OutputStream outputStream;
            try {
                rFile = new RandomAccessFile(originFile, "r");
                byte[] buffer = new byte[byteSize];
                rFile.seek(startPos); // 移动文件指针到每段开头
                int s = rFile.read(buffer);
                outputStream = new FileOutputStream(partFileName);
                outputStream.write(buffer, 0, s);
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 合并文件
     * @param dirPath        拆分文件所在的目录名
     * @param partFileSuffix 拆分文件的后缀名
     * @param partFileSize   拆分文件的字节数大小
     * @param mergeFileName  合并后的文件名
     */
    public static void mergePartFiles(String dirPath, String partFileSuffix,
                                      int partFileSize, String mergeFileName) throws IOException {
        ArrayList<File> partFiles = FileUtil.getDirFiles(dirPath, partFileSuffix);
        Collections.sort(partFiles, new FileComparator());
        RandomAccessFile rFile = new RandomAccessFile(mergeFileName, "rw");
        rFile.setLength(partFileSize * (partFiles.size() - 1) + partFiles.get(partFiles.size() - 1).length());
        rFile.close();
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
                partFiles.size(),
                partFiles.size() * 3,
                1,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(partFiles.size() * 2)
        );
        for (int i = 0; i < partFiles.size(); ++i) {
            threadPool.execute(new MergeTask(i * partFileSize,
                    mergeFileName, partFiles.get(i)));
        }
        threadPool.shutdown();
        while (!threadPool.isTerminated());
    }

    private static class MergeTask implements Runnable {
        long startPos;
        String mergeFileName;
        File partFile;

        public MergeTask(long startPos, String mergeFileName, File partFile) {
            this.startPos = startPos;
            this.mergeFileName = mergeFileName;
            this.partFile = partFile;
        }

        @Override
        public void run() {
            RandomAccessFile rFile;
            try {
                rFile = new RandomAccessFile(mergeFileName, "rw");
                rFile.seek(startPos);
                FileInputStream fis = new FileInputStream(partFile);
                byte[] buffer = new byte[fis.available()];
                fis.read(buffer);
                fis.close();
                rFile.write(buffer);
                rFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private static class FileComparator implements Comparator<File> {
        @Override
        public int compare(File o1, File o2) {
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    }


    /**
     * 获取指定目录下的文件列表
     */
    public static ArrayList<File> getDirFiles(String dirPath) {
        File path = new File(dirPath);
        File[] fileArr = path.listFiles();
        ArrayList<File> files = new ArrayList<File>();

        for (File f : fileArr) {
            if (f.isFile()) {
                files.add(f);
            }
        }
        return files;
    }

    /**
     * 获取指定目录下指定后缀名的文件列表
     *
     * @param dirPath 目录
     * @param suffix  后缀名
     * @return 文件列表
     */
    public static ArrayList<File> getDirFiles(String dirPath, final String suffix) {
        File path = new File(dirPath);
        File[] fileArr = path.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                String lowerName = name.toLowerCase();
                String lowerSuffix = suffix.toLowerCase();
                if (lowerName.endsWith(lowerSuffix)) {
                    return true;
                }
                return false;
            }
        });
        ArrayList<File> files = new ArrayList<File>();
        for (File f : fileArr) {
            if (f.isFile()) {
                files.add(f);
            }
        }
        return files;
    }


    /**
     * 以字符串的形式读取文件内容
     *
     * @param fileName 文件名
     * @return 文件内容
     * @throws IOException
     */
    public static String read(String fileName) throws IOException {
        File file = new File(fileName);
        FileInputStream fis = new FileInputStream(file);
        String result = null;
        byte[] buffer = new byte[fis.available()];
        fis.read(buffer);
        fis.close();
        result = new String(buffer);
        return result;
    }

    public static boolean write(String fileName, String content) throws IOException {
        boolean result = false;
        File file = new File(fileName);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(content.getBytes());
        fos.flush();
        fos.close();
        result = true;
        return result;
    }

    /**
     * 向指定文件追加内容
     * @param fileName 文件名
     * @param content  追加的内容
     * @return 成功返回true,否则返回false
     * @throws IOException
     */
    public static boolean append(String fileName, String content) throws IOException {
        boolean result = false;
        File file = new File(fileName);
        if (file.exists()) {
            RandomAccessFile rFile = new RandomAccessFile(file, "rw");
            byte[] buffer = content.getBytes();
            long originLen = file.length();
            rFile.setLength(originLen + buffer.length);
            rFile.seek(originLen);
            rFile.write(buffer);
            rFile.close();
            result = true;
        }
        return result;
    }

    // 获取不带路径的文件名
    public static String getSimpleFileName(String fileName) {
        //String separtor = System.getProperty("file.separator");
        String[] strs = fileName.split("/");
        String simpleFileName = strs[strs.length - 1];
        return simpleFileName;
    }

    /**
     * 文件分割（直接以合适的方式分割，不合并）
     * @param
     */
    public static List<String> splitBySuitSize(String fileName, int byteSize,List<Double> listNum) throws IOException {

        List<String> parts = new ArrayList<String>();
        File file = new File(fileName);
        int blockCount = (int) Math.ceil(file.length() / (double) byteSize);
        int countLen = (blockCount + "").length();
        // 用线程池来做这些事情
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
                blockCount,
                blockCount * 3,
                1, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(blockCount * 2)
        );
        for (int i = 0, size = 0; i < listNum.size(); ++i) {
            String partFileName = file.getName() + "." + leftPad((i + 1) + "", countLen, '0') + ".part";
            //size = (int)(listNum.get(i) * blockCount);
            if(listNum.size()-1 == i){
                threadPool.execute(new SplitSuitTask( ((int)(file.length() - size  * byteSize)) , partFileName, file, size * byteSize));
            }else {
                threadPool.execute(new SplitSuitTask(((int) (listNum.get(i) * blockCount)) * byteSize, partFileName, file, size * byteSize));
            }
            size += (int)(listNum.get(i) * blockCount) ;
            parts.add(partFileName);
        }
        threadPool.shutdown();
        while (!threadPool.isTerminated());
        return parts;
    }

    private static class SplitSuitTask implements Runnable {
        int  byteSize;       // 块大小
        String partFileName;
        File originFile;    // 源文件
        int startPos;       // 开始吃位置

        SplitSuitTask(int byteSize, String partFileName, File originFile, int startPos) {
            this.byteSize = byteSize;
            this.partFileName = partFileName;
            this.originFile = originFile;
            this.startPos = startPos;
        }

        @Override
        public void run() {
            RandomAccessFile rFile;
            OutputStream outputStream;
            try {
                rFile = new RandomAccessFile(originFile, "r");
                byte[] buffer = new byte[byteSize];
                rFile.seek(startPos); // 移动文件指针到每段开头
                int s = rFile.read(buffer);
                outputStream = new FileOutputStream(partFileName);
                outputStream.write(buffer, 0, s);
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
