package com.jlu.mcloud.utils;

/**
 * Created by koko on 17-3-20.
 */
public class Constant {
    public static final String TAG_TASK_ID = "taskID";
    public static final String TAG_TASK_FILE_PATH = "taskFilePath";
    public static final String TAG_TASK_FILE_FROM_NODEID = "taskFileFormNodeId";
    public static final String TAG_TASK_FILE_TIMESTAMP = "taskFileTimestamp";
    public static final String TAG_TASK_INDEX = "taskIndex";

    public static final String TABLE_HEARTBEAT = "heartbeat";
    public static final String TABLE_TASK = "taskTable";
    public static final String TABLE_USER = "userTable";

    public static final int MB = 1024 * 1024;
    public static final int KB = 1024;
    public static final long GB = 1024 * 1024 * 1024;

    public final static String RECEIVE_FILE_PATH = "/home/koko/mcloud/data";
    public final static String SEND_FILE_PATH = "";
    public final static int DEFAULT_FILE_SERVER_BIND_PORT = 10000;
}
