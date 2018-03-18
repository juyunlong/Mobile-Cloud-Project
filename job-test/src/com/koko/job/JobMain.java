package com.koko.job;

import com.jlu.mcloud.stdjob.BaseJob;

import java.util.Map;

/**
 * Created by koko on 17-3-21.
 */
public class JobMain implements BaseJob {

    @Override
    public Object start() {
        return "koko job start";
    }

    @Override
    public Object initJob(Map<String, Object> param) {
        return "koko job init";
    }

    @Override
    public Object stop() {
        return "koko job stop";
    }

}
