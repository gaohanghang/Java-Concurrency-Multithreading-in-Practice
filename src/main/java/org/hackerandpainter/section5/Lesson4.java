package org.hackerandpainter.section5;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description
 * @Author Gao Hang Hang
 * @Date 2019-07-25 20:56
 **/
public class Lesson4 {

    public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);

        // For Java 8, use Arrays.asList(...)
        List<String> initialElements = List.of("Ella", "Eclair", "Larry", "Felix");

        List<String> cats = new CopyOnWriteArrayList<>(initialElements);

        Runnable feedCats = () -> {
            try {
                for (String cat : cats) {
                    System.out.println("Feeding " + cat);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        scheduler.scheduleAtFixedRate(feedCats, 0, 100, TimeUnit.MILLISECONDS);

        // 社区猫号
        AtomicInteger communityCatNumber = new AtomicInteger(1);
        //这最终会导致ConcurrentModificationException
        Runnable adoptCommunityCat = () -> {
            try {
                cats.add("Community cat " + communityCatNumber.getAndIncrement());
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        scheduler.scheduleAtFixedRate(adoptCommunityCat, 1, 1000, TimeUnit.MILLISECONDS);
        scheduler.shutdown();

    }

}
