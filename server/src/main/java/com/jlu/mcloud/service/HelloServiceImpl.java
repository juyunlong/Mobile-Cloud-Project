package com.jlu.mcloud.service;

import java.net.ConnectException;
import java.util.Date;

/**
 * Created by koko on 2017/3/16.
 */
public class HelloServiceImpl implements HelloService {

    @Override
    public String sayHello() throws ConnectException {
        return (new Date(System.currentTimeMillis())).toString();
    }
}
