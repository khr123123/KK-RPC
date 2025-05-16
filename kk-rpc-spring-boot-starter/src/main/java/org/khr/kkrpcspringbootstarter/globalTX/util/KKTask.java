package org.khr.kkrpcspringbootstarter.globalTX.util;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class KKTask {

    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();


    public void waitTask() {
        try {
            lock.lock();
            condition.await();
        } catch (InterruptedException e) {
            log.error("waitTask error,{}", e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    public void signalTask() {
        lock.lock();
        condition.signal();
        lock.unlock();
    }
}
