package cn.pompip;

import org.junit.Test;

import java.util.LinkedList;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TestSum {
    public void log(String msg) {
        System.out.println(msg);
    }

    public void sleep(float second) {
        try {
            Thread.sleep((long) (second * 1000));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    LinkedList<String> queue = new LinkedList<>();
    Lock lock = new ReentrantLock();
    Condition condition = lock.newCondition();
    int i = 0;

    class Producer implements Runnable {

        @Override
        public void run() {


            try {
                lock.lock();
                for (;;) {
                    queue.add("" + i);
                    log("add : " + i);
                    i++;
                    sleep(0.5f);
                    if (i % 10 == 0) {
                        condition.signalAll();
                        condition.await();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }


        }
    }

    class Consumer implements Runnable {

        @Override
        public void run() {

            try {
                lock.lock();
                for (;;) {
                    String s = queue.poll();
                    log("poll : " + s);
                    sleep(0.5f);
                    if (s == null) {
                        condition.signalAll();
                        condition.await();
                    }

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }

        }

    }


    @Test
    public void testHello() {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        pool.submit(new Producer());
        pool.submit(new Consumer());


        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                super.run();
                System.out.println("exit");
            }
        });
        CountDownLatch latch = new CountDownLatch(1);
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
