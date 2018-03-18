package com.jlu.mcloud.communicate.respond;

import com.jlu.mcloud.communicate.model.Respond;

import java.io.IOException;
import java.util.List;

/**
 * Created by koko on 17-3-23.
 */
public interface RespondHandler {

    public void sendRespond(Respond respond) throws IOException;

    public List<Respond> askRespond(String taskId) throws IOException;
}
