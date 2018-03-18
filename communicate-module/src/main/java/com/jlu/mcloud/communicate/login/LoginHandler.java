package com.jlu.mcloud.communicate.login;

import java.io.IOException;

/**
 * Created by koko on 17-3-22.
 */
public interface LoginHandler {
    public int LOGIN_SUCESS = 1;
    public int LOGIN_ERROR_USERNAME = 2;
    public int LOGIN_ERROR_PASSWORD = 3;

    public int login(String username, String passwd) throws IOException;
}
