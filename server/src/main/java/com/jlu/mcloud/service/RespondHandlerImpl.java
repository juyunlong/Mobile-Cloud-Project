package com.jlu.mcloud.service;

import com.jlu.mcloud.communicate.model.Respond;
import com.jlu.mcloud.communicate.respond.RespondHandler;
import com.jlu.mcloud.manager.TaskRespondManager;

import java.io.IOException;
import java.util.List;

/**
 * Created by koko on 17-3-23.
 */
public class RespondHandlerImpl implements RespondHandler {

    private TaskRespondManager respondManager = TaskRespondManager.getInstance();

    @Override
    public void sendRespond(Respond respond) throws IOException {
        respondManager.addRespond(respond);
    }

    @Override
    public List<Respond> askRespond(String taskId) throws IOException {
        return respondManager.getResponds(taskId);
    }
}
