package org.hackerandpainter.section1;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;
import java.util.stream.IntStream;

/**
 * @Description
 * @Author Gao Hang Hang
 * @Date 2019-07-24 22:55
 **/
public class Lesson4 {

    public static void main(String[] args) {
        AppleTree[] appleTrees = AppleTree.newTreeGarden(12);
        PickFruitAction task = new PickFruitAction(appleTrees, 0, appleTrees.length - 1);

        ForkJoinPool pool = ForkJoinPool.commonPool();


    }

    /**
     * RecursiveAction代表没有返回值的任务。
     */
    public static class PickFruitAction extends RecursiveAction {

        private final AppleTree[] appleTrees;
        private final int startInclusive;
        private final int endInclusive;

        private final int taskThreadshold = 4;

        public PickFruitAction(AppleTree[] appleTrees, int startInclusive, int endInclusive) {
            this.appleTrees = appleTrees;
            this.startInclusive = startInclusive;
            this.endInclusive = endInclusive;
        }

        @Override
        protected void compute() {
            if (endInclusive - startInclusive < taskThreadshold) {
                doCompute();
                return;
            }
            int midpoint = startInclusive + (endInclusive - startInclusive) / 2;

            PickFruitAction leftSum = new PickFruitAction(appleTrees, startInclusive, midpoint);
            PickFruitAction rightSum = new PickFruitAction(appleTrees, midpoint + 1, endInclusive);

            rightSum.fork(); // 异步计算 computed asynchronously
            leftSum.compute();// 同步计算：立即和在当前线程中 computed synchronously: immediately and in the current thread
            rightSum.join();
        }

        protected void doCompute() {
            IntStream.rangeClosed(startInclusive,endInclusive)//
                    .forEach(i -> appleTrees[i].pickApples());
        }
    }
}
