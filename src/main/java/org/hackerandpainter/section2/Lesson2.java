package org.hackerandpainter.section2;

import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description
 * @Author Gao Hang Hang
 * @Date 2019-07-25 22:59
 **/
public class Lesson2 {

    private static final Runnable helloTask = //
            () -> System.out.printf("Hello from '%s'\n", Thread.currentThread().getName());

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // a program already has a thread - the main thread
        System.out.println("Current thread: " + Thread.currentThread().getName());

        //demoThread();
        //demoThreadsCreatedByThreadPool();
        //demoThreadFactory();
        //demoDifferentExecutorServices();
        demoScheduledExecutorService();
    }

    public static void demoThread() {
        System.out.println("Demo Thread");
        System.out.println(
                "⚠️For demo purpoeses only, don't create/start Threads yourself - use ExecutorService instead!!");

        // submit 10 similar tasks
        for (int i = 0; i < 10; i++) {
            new Thread(helloTask).start();
        }
        // 这些任务被10个不同的线程执行
        // The tasks are executed from _ten_ _different_ threads
        // 10 > 4 (4 is number of cores of my computer)
        // threads are NOT re-used

        System.out.println();
    }

    public static void demoThreadsCreatedByThreadPool() throws InterruptedException, ExecutionException {
        System.out.println("Demo ThreadPool");
        System.out.println("😄Use an ExecutorService to manage threads");

        ExecutorService pool = Executors.newCachedThreadPool();
        // 提交10个类似的任务，并观察它们是从不同执行的
        // submit 10 similar tasks and watch that they are executed from different
        // threads
        for (int i = 0; i < 10; i++) {
            pool.submit(helloTask);
        }

        // 不同于 thread.start(), threadPool.submit() 返回一个 Future
        // Unlike thread.start(), threadPool.submit() returns a Future
        Future<Integer> randomNumber = pool.submit(() -> new Random().nextInt());
        System.out.println("Random number: " + randomNumber.get());

        pool.shutdown();
        System.out.println();
    }

    public static void demoThreadFactory() {
        System.out.println("Demo ThreadFactory");
        System.out.println("😄Use an ExecutorService to manage threads");

        ThreadFactory threadFactory = new ThreadFactory() {
            private final AtomicInteger threadNumber = new AtomicInteger();

            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("Hello Thread " + threadNumber.getAndIncrement());
                thread.setPriority(Thread.MAX_PRIORITY);
                return thread;
            }
        };

        ExecutorService pool = Executors.newCachedThreadPool(threadFactory);

        // 提交10个类似的任务，并观察它们是从不同的任务执行的
        // submit 10 similar tasks and watch that they are executed from different
        // threads
        for (int i = 0; i < 10; i++) {
            pool.submit(helloTask);
        }

        pool.shutdown();
        System.out.println();
    }

    public static void demoDifferentExecutorServices() {
        System.out.println("Demo different thread pools");

        ExecutorService pool = Executors.newCachedThreadPool();
        // Try using these thread pools an how it influences the threads where the tasks
        // are executed
		//ExecutorService pool = Executors.newFixedThreadPool(5);
		//ExecutorService pool = Executors.newFixedThreadPool(1);
		//ExecutorService pool = Executors.newSingleThreadExecutor();

        // submit 10 similar tasks and watch that they are executed from different
        // threads
        for (int i = 0; i < 10; i++) {
            // Unlike thread.start(), threadPool.submit() returns a Future
            Future<?> result = pool.submit(helloTask);
        }

        // 确保在使用完毕后关闭线程池！
        // make sure to shut down the pool when finished using it!
        pool.shutdown();
        System.out.println();
    }

    // newScheduledThreadPool 创建一个定长线程池，支持定时及周期性任务执行。
    public static void demoScheduledExecutorService() {
        System.out.println("Demo scheduled tasks");

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);

        // 水提醒
        ScheduledFuture<?> waterReminder = scheduler.scheduleAtFixedRate(
                () -> System.out.println("Hi there, it's time to drink a glass of water"), //
                0, 1, TimeUnit.SECONDS);

        // 运动提醒
        ScheduledFuture<?> exerciseReminder = scheduler.scheduleAtFixedRate(
                () -> System.out.println("Hi there, it's time to exercise"), //
                0, 12, TimeUnit.SECONDS);

        // 在一定时间后取消任务
        Runnable canceller = () -> {
            exerciseReminder.cancel(false);
            waterReminder.cancel(false);
        };
        scheduler.schedule(canceller, 15, TimeUnit.SECONDS);
    }
}
