package com.jlu.mcloud.manager;

import com.jlu.mcloud.communicate.model.Respond;
import com.jlu.mcloud.communicate.model.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by koko on 17-3-23.
 */
public class TaskRespondManager {
    private List<Respond> responds = Collections.synchronizedList(new LinkedList<Respond>());

    private TaskRespondManager() {
    }

    public static TaskRespondManager getInstance() {
        return SingleHoler.INSTANCE;
    }

    private static class SingleHoler {
        public static final TaskRespondManager INSTANCE = new TaskRespondManager();
    }

    public void addRespond(Respond respond) {
        responds.add(respond);
    }

    public List<Respond> getResponds(String taskId) {
        List<Respond> subList = new ArrayList<Respond>();
        for (Respond respond : responds) {
            if (respond.getTaskId().equals(taskId)) {
                subList.add(respond);
            }
        }
        return subList;
    }

}
