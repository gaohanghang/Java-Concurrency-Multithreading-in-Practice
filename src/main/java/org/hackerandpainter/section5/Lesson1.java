package org.hackerandpainter.section5;

import org.hackerandpainter.section1.Lesson2;
import org.hackerandpainter.section1.Lesson4;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description
 * @Author Gao Hang Hang
 * @Date 2019-07-24 23:56
 **/
public class Lesson1 {

    public static void main(String[] args) throws InterruptedException {
        demoAtomicInteger();

        //// Atomic boolean in section 4
        //Lesson4.demoWaitNotify();
        //
        //// Atomic integer usage in section 2
        //Lesson2.demoThreadFactory();
    }

    public static void demoAtomicInteger() throws InterruptedException {
        int numberOfWorkers = 2;
        ExecutorService service = Executors.newFixedThreadPool(numberOfWorkers);

        Counter counter = new Counter();
        for (int i = 0; i < 10_000; i++) {
            service.submit(() -> counter.increment());
        }

        service.awaitTermination(1000, TimeUnit.MILLISECONDS);

        System.out.println("Increment 10_000 times: " + counter.getValue());
    }

    public static class Counter {
        private AtomicInteger value = new AtomicInteger();

        public void increment() {
            value.incrementAndGet();
            // this also works: value.addAndGet(1);
        }

        public void decrement() {
            value.decrementAndGet();
        }

        public int getValue() {
            return value.get();
        }
    }
}
