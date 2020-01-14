package cn.pompip.handle;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MessageQueue {
    Lock lock;
    Condition mEnptyQueue;
    Condition mFullQueue;
    Message[] mMessage;
    int putIndex;
    int takeIndex;
    int count;

    public MessageQueue() {
        lock = new ReentrantLock();
        mMessage = new Message[50];
        mEnptyQueue = lock.newCondition();
        mFullQueue = lock.newCondition();
    }

    void enqueueMessage(Message msg) {
        try {
            lock.lock();
            System.out.println("count:"+count +" length:"+mMessage.length);
            while (count == mMessage.length) {
                try {
                    mFullQueue.await();
                    System.out.println("await");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            mMessage[putIndex] = msg;
            putIndex = ++putIndex == mMessage.length ? 0 : putIndex;
            count++;
            mEnptyQueue.signalAll();

        } finally {
            lock.unlock();
        }
    }

    Message next() {
        Message message = null;
        try {
            lock.lock();
            while (count == 0) {
                try {
                    mEnptyQueue.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            message = mMessage[takeIndex];
            mMessage[takeIndex] = null;
            takeIndex = ++takeIndex == mMessage.length ? 0 : takeIndex;
            count--;
            mFullQueue.signalAll();
        } finally {
            lock.unlock();
        }
        return message;
    }
}
