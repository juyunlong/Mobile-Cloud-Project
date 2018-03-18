package com.jlu.mcloud.communicate.interfaces;

/**
 * Created by koko on 17-3-21.
 */
public interface ISubject {
    void registeObserver(IObserver observer);

    void removeObserver(IObserver observer);

    void notifyObservers();
}
