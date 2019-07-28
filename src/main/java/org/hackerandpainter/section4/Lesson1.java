package org.hackerandpainter.section4;

/**
 * @Description
 * @Author Gao Hang Hang
 * @Date 2019-07-27 22:35
 **/
public class Lesson1 {


    public static void main(String[] args) throws InterruptedException {
        demoThreadState();
    }

    private static void demoThreadState() throws InterruptedException {
        System.out.println("Main thread: " + Thread.currentThread().getState());
        System.out.println();

        Runnable sayHello = () -> {
            System.out.println("     Hi there!");
        };
        Thread thread = new Thread(sayHello);

        // nothing happens until the thread starts
        System.out.println("After creation: " + thread.getState());

        thread.start();
        System.out.println("After thread.start(): " + thread.getState());

        // 等到第二个线程通过sleep或by完成执行
        // Wait until the second thread completes its execution either by sleeping or by
        // joining
        thread.join();
        // or
        try {
            Thread.sleep(500, 0); // == TimeUnit.MILLISECONDS.sleep(1000)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("When completed execution: " + thread.getState());
    }
}
