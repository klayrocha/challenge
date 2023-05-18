package com.dws.challenge.service;


import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class responsible for managing concurrence
 *
 * @param <T>
 * @author klayrocha
 */
public class EntityLockWrapper<T extends EntityLockWrapper.Lockable> {

    private T entity;

    public interface Lockable {
        ReentrantLock getLock();
    }

    public EntityLockWrapper(T entity) {
        this.entity = entity;
    }

    public void tryLock() {
        tryLockOrError();
        lockIsMineOrError();
    }

    public void unlock() {
        entity.getLock().unlock();
    }

    private void lockIsMineOrError() {
        if (!entity.getLock().isHeldByCurrentThread()) {
            throw new RuntimeException("Error lock isn't mine");
        }
    }

    private void tryLockOrError() {
        try {
            entity.getLock().tryLock(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException("Error to get lock");
        }
    }
}

