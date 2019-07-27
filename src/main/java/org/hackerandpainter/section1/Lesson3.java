package org.hackerandpainter.section1;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;
import java.util.stream.IntStream;

/**
 * @Description
 * @Author Gao Hang Hang
 * @Date 2019-07-24 20:50
 **/
public class Lesson3 {

    public static void main(String[] args) {
        AppleTree[] appleTrees = AppleTree.newTreeGarden(12);
        ForkJoinPool pool = ForkJoinPool.commonPool();

        PickFruitTask task = new PickFruitTask(appleTrees, 0, appleTrees.length - 1);
        int result = pool.invoke(task);

        System.out.println();
        System.out.println("Total apples picked: " + result);
    }

    /**
     * RecursiveTask代表有返回值的任务
     */
    public static class PickFruitTask extends RecursiveTask<Integer> {

        private final AppleTree[] appleTrees;
        private final int startInclusive;
        private final int endInclusive;

        private final int taskThreadshold = 4;

        public PickFruitTask(AppleTree[] appleTrees, int startInclusive, int endInclusive) {
            this.appleTrees = appleTrees;
            this.startInclusive = startInclusive;
            this.endInclusive = endInclusive;
        }

        @Override
        protected Integer compute() {
            if (endInclusive - startInclusive < taskThreadshold) {
                return doCompute();
            }
            int midpoint = startInclusive + (endInclusive - startInclusive) / 2;

            PickFruitTask leftSum = new PickFruitTask(appleTrees, startInclusive, midpoint);
            PickFruitTask rightSum = new PickFruitTask(appleTrees, midpoint + 1, endInclusive);

            rightSum.fork(); // 异步计算 computed asynchronously

            return leftSum.compute() // 同步计算：立即和在当前线程中 computed synchronously: immediately and in the current thread
                    + rightSum.join();
        }

        protected Integer doCompute() {
            return IntStream.rangeClosed(startInclusive, endInclusive)
                    .map(i -> appleTrees[i].pickApples())
                    .sum();


//          Equivalent with a "for" loop :)
//			int result = 0;
//			for (int i = startInclusive; i <= endInclusive; i++) {
//				result += array[i].pickApples();
//			}
//			return result;
        }
    }
}
