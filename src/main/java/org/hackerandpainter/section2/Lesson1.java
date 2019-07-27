package org.hackerandpainter.section2;

import java.util.concurrent.*;

/**
 * @Description
 * @Author Gao Hang Hang
 * @Date 2019-07-25 21:43
 **/
public class Lesson1 {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        demoFutureWithCallable();
        //demoCallableVsRunnable();
    }

    public static void demoFutureWithCallable() throws InterruptedException, ExecutionException {
        System.out.println();
        System.out.println("Demo Future with Callable");
        ExecutorService pool = Executors.newCachedThreadPool();

        // Callable 执行任务后可返回值
        Future<Pizza> pizzaPickupOrder = pool.submit(() -> {
            System.out.println("   Restaurant> Slicing tomatoes");
            System.out.println("   Restaurant> Chopping onions");
            System.out.println("   Restaurant> Spreading with tomato sauce and sprinkle with toppings");
            System.out.println("   Restaurant> Baking pizza");
            TimeUnit.MILLISECONDS.sleep(300);
            return new Pizza();
        });

        System.out.println("Me: Call my brother");
        TimeUnit.MILLISECONDS.sleep(200);
        System.out.println("Me: Walk the dog");

        // Try this: pizzaPickupOrder.cancel(true);
        if (pizzaPickupOrder.isCancelled()) {
            System.out.println("Me: pizza is cancelled, order something else");
            System.out.println("pizzaPickupOrder.isDone(): " + pizzaPickupOrder.isDone());
        } else if (!pizzaPickupOrder.isDone()) {
            System.out.println("Me: Watch a TV show");
        }
        Pizza pizza = pizzaPickupOrder.get();

        System.out.println("Me: Eat the pizza: " + pizza);

        pool.shutdown();
        System.out.println();
        System.out.println();
    }

    public static void demoCallableVsRunnable() throws InterruptedException, ExecutionException {
        System.out.println();
        System.out.println("Demo: Callable vs Runnable");
        ExecutorService pool = Executors.newCachedThreadPool();

        // Runnable 执行任务后不可返回值
        Runnable makePizza = () -> {
            System.out.println("   Restaurant> Slicing tomatoes");
            System.out.println("   Restaurant> Chopping onions");
            System.out.println("   Restaurant> Spreading with tomato sauce and sprinkle with toppings");
            System.out.println("   Restaurant> Baking pizza");
            // 与Callable比较：需要在这里处理异常
            // Compare to Callable: need to handle exception here
            try {
                TimeUnit.MILLISECONDS.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //与Callable比较：无返回值
            // Compare to Callable: nothing to return
        };

        // compare to submit(Callable): Future<?> here vs Future<T> there
        Future<?> pizzaPickupOrder = pool.submit(makePizza);

        // try this: pool.execute(makePizza);

        System.out.println("Me: Calling my brother");
        TimeUnit.MILLISECONDS.sleep(200);
        System.out.println("Me: Walk the dog");

        Object pizza = pizzaPickupOrder.get(); // null
        System.out.println("Me: Eat the pizza: " + pizza);

        pool.shutdown();
    }

    public static class Pizza {

        @Override
        public String toString() {
            // 经典 margherita
            return "Classic margherita";
        }

    }

}
