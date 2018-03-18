package com.jlu.mcloud.service;

import com.jlu.mcloud.communicate.fileio.FileEntity;
import com.jlu.mcloud.communicate.fileio.FileIoHandler;
import com.jlu.mcloud.communicate.fileio.FileWriter;
import com.jlu.mcloud.communicate.model.Task;
import com.jlu.mcloud.config.Config;
import com.jlu.mcloud.utils.Constant;
import com.jlu.mcloud.db.mongo.MongoManager;
import com.jlu.mcloud.manager.TaskManager;
import com.jlu.mcloud.utils.FileUtil;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by koko on 17-3-20.
 */
public class FileIoHandlerImpl implements FileIoHandler {

    private TaskManager taskManager = TaskManager.getInstance();

    @Override
    public FileEntity readFile(String taskId) throws IOException {
        return getFileEntity(taskId);
    }

    @Override
    public String writeFile(FileEntity fileEntity) throws IOException {
        String simpleFileName = "TASK_" + Config.getTaskIndex() + "_" + fileEntity.getFileName();
        String fullFileName = Config.BASE_DIR + System.getProperty("file.separator") + simpleFileName;
        insertToMongo(fileEntity, fullFileName);
        writeToLocalFileSystem(fileEntity, fullFileName);
        String taskID = noticeTaskManager(fileEntity, simpleFileName);
        return taskID;
    }

    // 通知TaskManager有新任务到达
    private String noticeTaskManager(FileEntity fileEntity, String fileName) {
        Task task = new Task();
        task.setDone(false);
        task.setTaskFileName(fileName);
        task.setTaskFromNodeId(fileEntity.getNodeId());
        task.setTaskId(fileEntity.getNodeId() + "_" + fileEntity.getTime() + "_" + Config.getTaskIndex());
        task.setTaskIndex(Config.getTaskIndex());
        taskManager.addTask(task);
        Config.incrementTaskIndex();
        return task.getTaskId();
    }

    // 根据提供的 fileId 从MongoDB中找到文件并实例化成FileEntity
    private FileEntity getFileEntity(String taskId) throws IOException {
        FileEntity fileEntity = new FileEntity();
        MongoManager mongoManager = new MongoManager();
        MongoCollection<Document> collection = mongoManager.getDBCollection("taskTable");
        FindIterable<Document> iterable = collection.find(Filters.eq(Constant.TAG_TASK_ID, taskId));
        if (iterable.iterator().hasNext()) {
            Document document = iterable.iterator().next();
            File file = new File(document.getString(Constant.TAG_TASK_FILE_PATH));
            byte[] buffer = FileWriter.toArrayByte(file);
            fileEntity.setFileName(file.getName());
            fileEntity.setData(buffer);
            fileEntity.setErrorMsg("NO_ERROR");
            fileEntity.setNodeId(document.getString(Constant.TAG_TASK_FILE_FROM_NODEID));
            fileEntity.setTime(System.currentTimeMillis());
            fileEntity.setLength(file.length());
        }
        return fileEntity;
    }

    // 将文件信息保存到 MongoDB 中
    private void insertToMongo(FileEntity fileEntity, String fileName) {
        MongoManager mongoManager = new MongoManager();
        MongoCollection<Document> collection = mongoManager.getDBCollection(Constant.TABLE_TASK);
        Document document = new Document();
        document.append(
                Constant.TAG_TASK_ID,
                fileEntity.getNodeId() + "_" + fileEntity.getTime() + "_" + Config.getTaskIndex()
        );
        document.append(Constant.TAG_TASK_FILE_FROM_NODEID, fileEntity.getNodeId());
        document.append(Constant.TAG_TASK_FILE_PATH, fileName);
        document.append(Constant.TAG_TASK_INDEX, Config.getTaskIndex());
        document.append(Constant.TAG_TASK_FILE_TIMESTAMP, fileEntity.getTime());
        collection.insertOne(document);
    }

    //将文件写入到本地文件系统
    private void writeToLocalFileSystem(final FileEntity fileEntity, final String fileName) throws IOException {
        FileOutputStream outputStream = null;
        outputStream = new FileOutputStream(fileName);
        outputStream.write(fileEntity.getData());
        outputStream.close();
    }

}
