package com.jlu.mcloud.stdjob;

import java.util.Map;

/**
 * Created by koko on 17-3-21.
 */
public interface BaseJob {
    Object start();

    Object initJob(Map<String, Object> param);

    Object stop();
}
