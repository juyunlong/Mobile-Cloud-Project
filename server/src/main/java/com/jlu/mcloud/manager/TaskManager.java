package com.jlu.mcloud.manager;


import com.jlu.mcloud.communicate.interfaces.IObserver;
import com.jlu.mcloud.communicate.interfaces.ISubject;
import com.jlu.mcloud.communicate.model.Task;
import com.jlu.mcloud.db.mongo.MongoManager;
import com.jlu.mcloud.util.MCLogger;
import com.jlu.mcloud.utils.Constant;
import com.jlu.mcloud.utils.FileUtil;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.io.File;
import java.util.*;

/**
 * Created by koko on 17-3-20.
 * 此处用到了观察者模式
 */
public class TaskManager implements ISubject {

    private static ConnectionManager connectionManager = ConnectionManager.getInstance();
    private List<Task> taskList = Collections.synchronizedList(new LinkedList<Task>());
    private IObserver taskFactory = (IObserver) TaskFactory.getInstance();
    private Map<String, Boolean> downloadList = new HashMap<String, Boolean>();
    private static Map<String, Float> taskAllocRate = new HashMap<String, Float>();
    private static Map<String, String> taskAllocFile = new HashMap<String, String>();

    private TaskManager() {
    }

    private static class SingleHolder {
        private static final TaskManager INSTANCE = new TaskManager();
    }

    public void addTask(Task task) {
        TaskAllocator allocator = new TaskAllocator();
        allocator.alloc(task.getTaskFromNodeId());
        taskList.add(task);
        notifyObservers();
    }

    public void removeTask(int index) {
        taskList.remove(index);
    }



    public static TaskManager getInstance() {
        return SingleHolder.INSTANCE;
    }

    @Override
    public void registeObserver(IObserver observer) {

    }

    @Override
    public void removeObserver(IObserver observer) {

    }

    @Override
    public void notifyObservers() {
        taskFactory.update(taskList.get(taskList.size() - 1));
    }

    /**
     * 内部类。负责任务分配的计算
     */
    public static class TaskAllocator {
        public TaskAllocator() {
        }
        /**
         * 任务分配
         */
        public void alloc(String fromId) {
            List<String> onlineNode = connectionManager.getAllOnlineNode();
            // 临时map用于存储每个设备的归一化值，用于后面计算任务比例
            Map<String, Float> tmp = new HashMap<String, Float>();
            float totalAlpha = 0.0F;
            for (String nodeId : onlineNode) {
                if (!nodeId.equals(fromId)) {
                    try {
                        NodeInfo info = getNodeById(nodeId);
                        float alpha = calculateAlpha(info);
                        totalAlpha += alpha;
                        tmp.put(nodeId, alpha);
                    } catch (RuntimeException e) {
                        MCLogger.log("Allocate error!!!");
                        e.printStackTrace();
                    }
                }
            }
            // 根据归一化值为每个在线设备计算任务比例，任务发送方设置为0
            for (String nodeId : onlineNode) {
                if (nodeId.equals(fromId)) {
                    taskAllocRate.put(fromId, 0.0f);
                } else {
                    float rate = tmp.get(nodeId) / totalAlpha;
                    taskAllocRate.put(nodeId, rate);
                }
            }
        }

        public void allocData() {
            ArrayList<File> files = FileUtil.getDirFiles("", ".part");
            int count = files.size();
            for (Map.Entry<String, Float> entry : taskAllocRate.entrySet()) {
                int size = (int) (count * entry.getValue());
            }

        }

        /**
         * 根据设备ID找到对应的设备参数
         * @param nodeId
         * @return
         * @throws RuntimeException
         */
        private NodeInfo getNodeById(String nodeId) throws RuntimeException{
            MongoManager manager = new MongoManager();
            MongoCollection<Document> collection = manager.getDBCollection(Constant.TABLE_HEARTBEAT);
            FindIterable<Document> findIterable = collection.find(Filters.eq("_id", nodeId));
            if (findIterable.iterator().hasNext()) {
                NodeInfo nodeInfo = new NodeInfo();
                Document document = findIterable.iterator().next();
                nodeInfo.leftPower = Integer.parseInt(document.getString("leftPower"));
                nodeInfo.availMem = document.getLong("availMemory");
                nodeInfo.CPUFreq = Long.parseLong(document.getString("cpuFrequency"));
                return nodeInfo;
            } else {
                throw new RuntimeException("No id matched");
            }
        }

        /**
         * 根据设备参数计算归一化值，此处需要后期算法支持
         * @return
         */
        public float calculateAlpha(NodeInfo info) {
            //TODO: 后期算法支持
            float alpha = (float) (info.availMem * 0.5 + info.CPUFreq * 0.3 + info.leftPower * 0.2);
            return alpha;
        }

        public float getRateById(String nodeId) {
            return taskAllocRate.get(nodeId);
        }

    }

    private static class NodeInfo {
        long availMem;
        int leftPower;
        long CPUFreq;

        NodeInfo() {
        }

        NodeInfo(int availMem, int leftPower, long CPUFreq) {
            this.availMem = availMem;
            this.leftPower = leftPower;
            this.CPUFreq = CPUFreq;
        }
    }
}
