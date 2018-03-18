package com.jlu.mcloud.rpc.server;

import com.jlu.mcloud.rpc.config.Config;
import com.jlu.mcloud.util.MCLogger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by koko on 2017/3/15.
 */
public class ServiceCenter implements RPCServer {

    private static ExecutorService executor = Executors.newFixedThreadPool(Config.NTHREAD);
    private final ConcurrentHashMap<String, Class> serviceRegistry = new ConcurrentHashMap<String, Class>();
    private AtomicBoolean isRunning = new AtomicBoolean(true);
    private int port = Config.PORT;
    private List<Connection> connectionList = Collections.synchronizedList(new LinkedList<Connection>());
    private static int connectionNum = 0;

    private ServiceCenter() {
    }

    // 单例模式
    private static class SingleHolder {
        private static final ServiceCenter INSTANCE = new ServiceCenter();
    }

    public static ServiceCenter getInstance() {
        return SingleHolder.INSTANCE;
    }

    public static ServiceCenter getInstance(int port) {
        SingleHolder.INSTANCE.port = port;
        return SingleHolder.INSTANCE;
    }


    @Override
    public void stop() {
        isRunning.set(false);
        executor.shutdown();
        MCLogger.info("RPC Server has been stoped");
    }

    @Override
    public void start() throws IOException {
        ServerSocket server = new ServerSocket();
        server.bind(new InetSocketAddress(port));
        MCLogger.info("Server start at " + port);

        try {
            while (true) {
                // 监听客户端的TCP连接，接到TCP连接后将其封装成task，由线程池执行
                Socket socket = server.accept();
                executor.execute(new ServiceTask(socket));
                connectionList.add(connectionNum, new Connection(socket, System.currentTimeMillis()));
                connectionNum++;
            }
        } catch (Exception e) {
            // Do nothing just print the exception message
            e.printStackTrace();
        } finally {
            server.close();
        }
    }

    @Override
    public void register(Class serviceInterface, Class impl) {
        MCLogger.info("Regist service " + serviceInterface.getName());
        serviceRegistry.put(serviceInterface.getName(), impl);
    }

    @Override
    public boolean isRunning() {
        return isRunning.get();
    }

    @Override
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public ConcurrentHashMap<String, Class> getServiceRegistry() {
        return serviceRegistry;
    }

    private class ServiceTask implements Runnable {
        private Socket client;

        ServiceTask(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            ObjectInputStream input = null;
            ObjectOutputStream output = null;

            try {
                // 将客户端发送的码流反序列化成对象，反射调用服务实现者，获取执行结果
                input = new ObjectInputStream(client.getInputStream());
                String serviceName = input.readUTF();
                String methodName = input.readUTF();

                MCLogger.info("Response for service " + serviceName + ":" + methodName);

                Class<?>[] parameterTypes = (Class<?>[]) input.readObject();
                Object[] arguments = (Object[]) input.readObject();
                Class serviceClass = serviceRegistry.get(serviceName);

                if (serviceClass == null) {
                    throw new ClassNotFoundException(serviceName + " not fonnd");
                }

                Method method = serviceClass.getMethod(methodName, parameterTypes);
                Object result = method.invoke(serviceClass.newInstance(), arguments);

                // 将执行结果反序列化，通过socket发送给客户端
                output = new ObjectOutputStream(client.getOutputStream());
                output.writeObject(result);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (output != null) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public class Call {
    }

    public class Connection {
        private Socket socket;
        private long timestamp;

        public Connection(Socket socket, long timestamp) {
            this.socket = socket;
            this.timestamp = timestamp;
        }

        public Socket getSocket() {
            return socket;
        }

        public void setSocket(Socket socket) {
            this.socket = socket;
        }


        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }
}

