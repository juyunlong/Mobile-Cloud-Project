package com.jlu.mcloud.manager;


import com.jlu.mcloud.communicate.interfaces.IFactory;
import com.jlu.mcloud.communicate.interfaces.IObserver;
import com.jlu.mcloud.communicate.interfaces.IProduct;
import com.jlu.mcloud.communicate.model.Task;

/**
 * Created by koko on 17-3-21.
 */
public class TaskFactory implements IFactory, IObserver {

    //private List<Task> taskList = Collections.synchronizedList(new LinkedList<Task>());
    private static Task task = new Task();
    //private TaskManager manager = TaskManager.getInstance();
    private TaskManager.TaskAllocator allocator = new TaskManager.TaskAllocator();
    private TaskFactory() {
    }

    private static class SingleHolder {
        public static final TaskFactory INSTANCE = new TaskFactory();

    }

    public static TaskFactory getInstance() {
        return SingleHolder.INSTANCE;
    }

    @Override
    public IProduct createProduct(String nodeId) {
        if (task.getTaskIndex() != -1) {
            task.setRate(allocator.getRateById(nodeId));
        }
        return task;
    }

    @Override
    public void update(Object object) {
        task = (Task) object;
    }
}
