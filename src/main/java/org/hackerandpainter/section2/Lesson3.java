package org.hackerandpainter.section2;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author Gao Hang Hang
 * @Date 2019-07-26 21:23
 **/
public class Lesson3 {

    public static void main(String[] args) {
        // 未提供executor，CompletableFuture.supplyAsync默认使
        // executor is not provided, CompletableFuture.supplyAsync will use ForkJoinPool by default
        ExecutorService executor = Executors.newCachedThreadPool();

        final String tomatoes = "Tomatoes";
        // 使用 supplyAsync() 运行一个异步任务并且返回结果, 使用handle()方法处理异常
        CompletableFuture<String> sliceTomatoes = CompletableFuture.supplyAsync(() -> {
//			try {
//				TimeUnit.MILLISECONDS.sleep(10);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
            System.out.println("   Restaurant> Slicing tomatoes");
            if (tomatoes == null) {
                throw new RuntimeException("No tomatoes");
            }
            return "Tomatoes ";
        }, executor).handle((result, e) -> {
            if (result == null) {
                System.out.println("Problems with tomatoes");
                return "";
            }
            return result;
        });

        // 切洋葱
        CompletableFuture<String> chopOnions = CompletableFuture.supplyAsync(() -> {
            System.out.println("   Restaurant> Chopping onions");
            return "Onions ";
        }, executor);

        // 准备原料 使用thenCombine()组合两个独立的 future thenCombine()被用来当两个独立的Future都完成的时候，用来做一些事情。
        // 切西红柿和切洋葱都完成后
        CompletableFuture<String> prepIngredients = sliceTomatoes.thenCombine(chopOnions, //
                String::concat);

        // 可以使用 thenApply() 处理和改变CompletableFuture的结果。持有一个Function<R,T>作为参数。Function<R,T>是一个简单的函数式接口，接受一个T类型的参数，产出一个R类型的结果。
        // 准备披萨
        CompletableFuture<Object> prepPizza = prepIngredients.thenApply(toppings -> {
            System.out.println("   Restaurant> Spreading with tomato sauce and sprinkle with toppings: " + toppings);
            return "Raw pizza with " + toppings;
        });

        // 烤披萨
        CompletableFuture<String> bakePizza = prepPizza.thenApply(rawPizza -> {
            System.out.println("   Restaurant> Baking pizza: " + rawPizza);
            try {
                TimeUnit.MILLISECONDS.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Pizza";
        });

        bakePizza.thenAccept(pizza -> System.out.println("Eating pizza: " + pizza));
        // or, the old way  // System.out.println(bakePizza.get());
    }

}
