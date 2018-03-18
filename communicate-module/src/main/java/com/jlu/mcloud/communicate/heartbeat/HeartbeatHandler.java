package com.jlu.mcloud.communicate.heartbeat;

import java.net.ConnectException;

/**
 * Created by koko on 2017/3/16.
 */
public interface HeartbeatHandler {
    public Command sendHeartbeat(HeartbeatPacket packet) throws ConnectException;
}
