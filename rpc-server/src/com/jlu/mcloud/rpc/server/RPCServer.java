package com.jlu.mcloud.rpc.server;

import java.io.IOException;

/**
 * Created by koko on 2017/3/15.
 */
public interface RPCServer {
    public void stop();

    public void start() throws IOException;

    public void register(Class serviceInterface, Class impl);

    public boolean isRunning();

    public int getPort();
}
