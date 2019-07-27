package org.hackerandpainter.section5;

import java.util.Random;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @Description
 * @Author Gao Hang Hang
 * @Date 2019-07-25 00:09
 **/
public class Lesson2 {

    public static void main(String[] args) throws InterruptedException {
         //demoConcurrentHashMap();
        demoConcurrentLinkedQueue();
    }

    private static void demoConcurrentHashMap() throws InterruptedException {
        Random random = new Random();
        ExecutorService service = Executors.newCachedThreadPool();

        String brandNewShoes = "Brand new shows";
        String oldPhone = "Old phone";
        String leatherHat = "Leather hat";
        String cowboyShoes = "Cowboy shoes";

        ConcurrentMap<String, String> itemToBuyerMap = new ConcurrentHashMap<>();

        BiConsumer<String, String> buyItemIfNotTaken = (buyer, item) -> {
            try {
                TimeUnit.MILLISECONDS.sleep(random.nextInt(1000));
                itemToBuyerMap.putIfAbsent(item, buyer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        service.submit(() -> {
            buyItemIfNotTaken.accept("Alice", brandNewShoes);
            buyItemIfNotTaken.accept("Alice", cowboyShoes);
            buyItemIfNotTaken.accept("Alice", leatherHat);
        });

        service.submit(() -> {
            buyItemIfNotTaken.accept("Bob", brandNewShoes);
            buyItemIfNotTaken.accept("Bob", cowboyShoes);
            buyItemIfNotTaken.accept("Bob", leatherHat);
        });

        service.submit(() -> {
            buyItemIfNotTaken.accept("Carol", brandNewShoes);
            buyItemIfNotTaken.accept("Carol", cowboyShoes);
            buyItemIfNotTaken.accept("Carol", leatherHat);
        });

        service.awaitTermination(2000, TimeUnit.MILLISECONDS);

        itemToBuyerMap
                .forEach((item, buyer) -> System.out.printf("%s bought by %s%n", item, buyer));
    }

    private static void demoConcurrentLinkedQueue() throws InterruptedException {
        Random random = new Random();
        /**
         * Java通过Executors提供四种线程池，分别为：
         * newCachedThreadPool创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。
         * newFixedThreadPool 创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待。
         * newScheduledThreadPool 创建一个定长线程池，支持定时及周期性任务执行。
         * newSingleThreadExecutor 创建一个单线程化的线程池，它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行。
         */
        ExecutorService service = Executors.newCachedThreadPool();


        ConcurrentLinkedDeque<String> queue = new ConcurrentLinkedDeque<>();
        Consumer<String> joinQueue = (name) -> {
            try {
                TimeUnit.MILLISECONDS.sleep(random.nextInt(1000));
                queue.offer(name);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        service.submit(() -> joinQueue.accept("Alice"));
        service.submit(() -> joinQueue.accept("Bob"));
        service.submit(() -> joinQueue.accept("Carol"));
        service.submit(() -> joinQueue.accept("Daniel"));

        // Try null:
        // service.submit(() -> joinQueue.accept(null));

        // wait so at least several elements are in the queue
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
        }

        service.submit(() -> System.out.println("poll(): " + queue.poll()));
        service.submit(() -> System.out.println("poll(): " + queue.poll()));
        service.submit(() -> System.out.println("poll(): " + queue.poll()));
        service.submit(() -> System.out.println("poll(): " + queue.poll()));
        service.submit(() -> System.out.println("poll(): " + queue.poll()));

        service.awaitTermination(2000, TimeUnit.MILLISECONDS);

        // 剩余元素
        System.out.println("\n    Remaing elements: ");
        queue.forEach((name) -> {
            System.out.println(name);
        });
    }
}
