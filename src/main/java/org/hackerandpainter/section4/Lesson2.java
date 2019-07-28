package org.hackerandpainter.section4;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author Gao Hang Hang
 * @Date 2019-07-27 22:39
 **/
public class Lesson2 {

    public static void main(String[] args) throws InterruptedException {
        demoSynchronized();
        demoBlockedState();
    }

    public static void demoSynchronized() throws InterruptedException {
        int numberOfWorkers = 2;
        ExecutorService service = Executors.newFixedThreadPool(numberOfWorkers);

        Counter counter = new Counter();
        for (int i = 0; i < 10_1000; i++) {
            service.submit(() -> counter.increment());
        }

        // 可以用awaitTermination()方法来判断线程池中是否有继续运行的线程。
        service.awaitTermination(1000, TimeUnit.MILLISECONDS);

        System.out.println("Increment 10_000 times: " + counter.getValue());
    }

    public static class Counter {
        private final Object lock = new Object();

        private int value = 0;

        public synchronized void increment() {
            // Try this after removing "synchronized void"
            // synchronized (this) {
            // Also try this:
            // synchronized (lock) {
            value++;
            // }
        }

        public synchronized void decrement() {
            value--;
        }

        public static void staticMethodCanBeSynchronizedToo() {
            synchronized (Counter.class) {
                System.out.println("A static method can be synchronized too");
            }
        }

        public static synchronized void staticMethodCanBeSynchronizedToo2() {
            System.out.println("A static method can be synchronized too");
        }

        public synchronized int getValue() {
            return value;
        }
    }

    public static void demoBlockedState() {
        System.out.println();
        final Object lock = new Object();

        // 线程认为等一下
        Runnable waitALittle = () -> {
            synchronized (lock) {
                System.out.println("Wait a little...");
                while (true) {
                    // it will take forever!
                    // let's block everything!!
                }
            }
        };

        Thread iAmBusy = new Thread(waitALittle);
        Thread iAmBusyToo = new Thread(waitALittle);

        iAmBusy.start();
        iAmBusyToo.start();

        try {
            TimeUnit.MILLISECONDS.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("First thread: " + iAmBusy.getState());
        System.out.println("Second thread: " + iAmBusyToo.getState());
    }

}
