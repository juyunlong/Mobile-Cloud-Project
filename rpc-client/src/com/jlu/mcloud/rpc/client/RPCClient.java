package com.jlu.mcloud.rpc.client;

import com.jlu.mcloud.util.MCLogger;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by koko on 17-3-19.
 */
public class RPCClient<T> {

    public static <T> T getRemoteProxyObject(final Class<?> serviceInterface, final InetSocketAddress address) {

        MCLogger.info("Request service " + serviceInterface.getName());
        return (T) Proxy.newProxyInstance(serviceInterface.getClassLoader(), new Class<?>[]{serviceInterface}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Socket socket = null;
                ObjectInputStream input = null;
                ObjectOutputStream output = null;
                try {
                    socket = new Socket();
                    socket.connect(address);
                    output = new ObjectOutputStream(socket.getOutputStream());
                    output.writeUTF(serviceInterface.getName());
                    output.writeUTF(method.getName());
                    output.writeObject(method.getParameterTypes());
                    output.writeObject(args);
                    input = new ObjectInputStream(socket.getInputStream());
                    return input.readObject();
                } finally {
                    if (socket != null) socket.close();
                    if (output != null) output.close();
                    if (input != null) input.close();
                }
            }
        });
    }
}
