package com.nikitin;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CrptApi {
    private static final Lock lock = new ReentrantLock();
    private static final AtomicInteger requestCount = new AtomicInteger(0);
    private static  long timeInterval = 0;


        public CrptApi(TimeUnit timeUnit, int requestLimit) {
            this.timeInterval = timeUnit.toMillis(1) / requestLimit;
        }

        public static void createDocument(Object document, String signature) throws InterruptedException {
            if (!tryAcquire()) {
                System.out.println("Превышено максимальное количество запросов. Запрос заблокирован.");
                return;
            }
            System.out.println("Запрос к API Честного знака выполнен успешно.");

            release();
        }
        private static boolean tryAcquire() throws InterruptedException {
            lock.lockInterruptibly();
            try {
                int currentCount = requestCount.incrementAndGet();
                if (currentCount == 1) {
                    return true;
                } else {
                    long startTime = System.currentTimeMillis();
                    long elapsedTime = 0;
                    while (elapsedTime < timeInterval) {
                        lock.unlock();
                        Thread.sleep(timeInterval - elapsedTime);
                        lock.lockInterruptibly();
                        elapsedTime = System.currentTimeMillis() - startTime;
                    }
                    currentCount = requestCount.getAndSet(1);
                    return currentCount == 0;
                }
            } finally {
                lock.unlock();
            }
        }
        private static void release() {
            lock.lock();
            try {
                int currentCount = requestCount.decrementAndGet();
                if (currentCount < 0) {
                    requestCount.set(0);
                }
            } finally {
                lock.unlock();
            }
        }
}
