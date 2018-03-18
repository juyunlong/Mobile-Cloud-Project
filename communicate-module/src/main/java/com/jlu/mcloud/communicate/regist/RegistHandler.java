package com.jlu.mcloud.communicate.regist;

import java.io.IOException;

/**
 * Created by koko on 17-3-22.
 */
public interface RegistHandler {
    int REGIST_SUCCESS = 1;
    int REGIST_ERROR_ALLREADY_REGIST = 2;
    int REGIST_ERROR_INVALID_USERNAME = 3;

    public int regist(String username, String passwd) throws IOException;
}
