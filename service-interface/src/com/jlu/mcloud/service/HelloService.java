package com.jlu.mcloud.service;


import java.net.ConnectException;

/**
 * Created by koko on 2017/3/16.
 */
public interface HelloService {
    public String sayHello() throws ConnectException;
}
