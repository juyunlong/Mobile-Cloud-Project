package com.jlu.mcloud.communicate.heartbeat;

import com.jlu.mcloud.communicate.config.Config;
import com.jlu.mcloud.rpc.client.RPCClient;
import com.jlu.mcloud.util.MCLogger;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by koko on 2017/3/16.
 */
public abstract class HeartbeatClient implements Runnable {

    private String serverIP = Config.IP;
    private int serverPort = Config.PORT;
    private String nodeId = UUID.randomUUID().toString();
    private AtomicBoolean isRunning = new AtomicBoolean(true);
    private long lastBeatTime = 0;
    private long heartBeatInterval = Config.HEARTBEATINTERVAL;

    @Override
    public void run() {

        while (isRunning.get()) {
            HeartbeatHandler handler;
            long startTime = System.currentTimeMillis();
            if ((startTime - lastBeatTime) > heartBeatInterval) {
                handler = RPCClient.getRemoteProxyObject(
                        HeartbeatHandler.class,
                        new InetSocketAddress(serverIP, serverPort)
                );
                lastBeatTime = startTime;
                HeartbeatPacket packet = new HeartbeatPacket();
                packet.setTime(startTime);
                packet.setNodeId(getNodeId());
                packet.setInfo(getInfo());
                // 向服务器发送心跳并返回需要执行的命令
                MCLogger.info("send a packet");
                Command cmd = null;
                try {
                    cmd = handler.sendHeartbeat(packet);
                    if (!processCommand(cmd)) {
                        // TODO : do something
                        continue;
                    }
                } catch (ConnectException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setInetAddress(String ip, int port) {
        this.serverIP = ip;
        this.serverPort = port;
    }


    public void stop() {
        MCLogger.info("heartbeat client has stopped");
        isRunning.set(false);
    }

    protected abstract boolean processCommand(Command cmd);

    protected Map<String, Object> getInfo() {
        return new HashMap<String, Object>();
    }

    protected String getNodeId() {
        return nodeId;
    }

}
