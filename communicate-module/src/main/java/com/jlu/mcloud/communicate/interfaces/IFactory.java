package com.jlu.mcloud.communicate.interfaces;

/**
 * Created by koko on 17-3-21.
 */
public interface IFactory {
    IProduct createProduct(String nodeId);
}
