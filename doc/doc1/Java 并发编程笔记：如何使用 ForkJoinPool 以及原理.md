# 前言

Java 1.7 引入了一种新的并发框架—— Fork/Join Framework。

本文的主要目的是介绍 ForkJoinPool 的适用场景，实现原理，以及示例代码。

*TLDR;* 如果觉得文章太长的话，以下就是**结论**：

- `ForkJoinPool` 不是为了替代 `ExecutorService`，而是它的补充，在某些应用场景下性能比 `ExecutorService` 更好。（见 *Java Tip: When to use ForkJoinPool vs ExecutorService* ）
- **ForkJoinPool 主要用于实现“分而治之”的算法，特别是分治之后递归调用的函数**，例如 quick sort 等。
- `ForkJoinPool` 最适合的是计算密集型的任务，如果存在 I/O，线程间同步，`sleep()` 等会造成线程长时间阻塞的情况时，最好配合使用 `ManagedBlocker`。

# 使用

首先介绍的是大家最关心的 Fork/Join Framework 的使用方法，如果对使用方法已经很熟悉的话，可以跳过这一节，直接阅读[原理](http://blog.dyngr.com/blog/2016/09/15/java-forkjoinpool-internals/#原理)。

用一个特别简单的求整数数组所有元素之和来作为我们现在需要解决的问题吧。

## 问题

> 计算1至1000的正整数之和。

## 解决方法

### For-loop

最简单的，显然是不使用任何并行编程的手段，只用最直白的 *for-loop* 来实现。下面就是具体的实现代码。

不过为了便于横向对比，也为了让代码更加 Java Style，首先我们先定义一个 interface。

```java
public interface Calculator {
    long sumUp(long[] numbers);
}
```

这个 interface 非常简单，只有一个函数 `sumUp`，就是返回数组内所有元素的和。

再写一个 `main` 方法。

```java
import doc1.Calculator;public class Main {
    public static void main(String[] args) {
        long[] numbers = LongStream.rangeClosed(1, 1000).toArray();
        Calculator calculator = new MyCalculator();
        System.out.println(calculator.sumUp(numbers)); // 打印结果500500
    }
}
```

接下来就是我们的 Plain Old For-loop Calculator，简称 *POFLC* 的实现了。（这其实是个段子，和主题完全无关，感兴趣的请见文末的[彩蛋](http://blog.dyngr.com/blog/2016/09/15/java-forkjoinpool-internals/#彩蛋)）

```java
import doc1.Calculator;ublic class ForLoopCalculator implements Calculator {
    public long sumUp(long[] numbers) {
        long total = 0;
        for (long i : numbers) {
            total += i;
        }
        return total;
    }
}
```

这段代码毫无出奇之处，也就不多解释了，直接跳入下一节——并行计算。

### ExecutorService

在 Java 1.5 引入 `ExecutorService` 之后，基本上已经不推荐直接创建 `Thread` 对象，而是统一使用 `ExecutorService`。毕竟从接口的易用程度上来说 `ExecutorService` 就远胜于原始的 `Thread`，更不用提 `java.util.concurrent` 提供的数种线程池，Future 类，Lock 类等各种便利工具。

使用 `ExecutorService` 的实现

```java
import doc1.Calculator;public class ExecutorServiceCalculator implements Calculator {
    private int parallism;
    private ExecutorService pool;

    public ExecutorServiceCalculator() {
        parallism = Runtime.getRuntime().availableProcessors(); // CPU的核心数
        pool = Executors.newFixedThreadPool(parallism);
    }

    private static class SumTask implements Callable<Long> {
        private long[] numbers;
        private int from;
        private int to;

        public SumTask(long[] numbers, int from, int to) {
            this.numbers = numbers;
            this.from = from;
            this.to = to;
        }

        @Override
        public Long call() throws Exception {
            long total = 0;
            for (int i = from; i <= to; i++) {
                total += numbers[i];
            }
            return total;
        }
    }

    @Override
    public long sumUp(long[] numbers) {
        List<Future<Long>> results = new ArrayList<>();

        // 把任务分解为 n 份，交给 n 个线程处理
        int part = numbers.length / parallism;
        for (int i = 0; i < parallism; i++) {
            int from = i * part;
            int to = (i == parallism - 1) ? numbers.length - 1 : (i + 1) * part - 1;
            results.add(pool.submit(new SumTask(numbers, from, to)));
        }

        // 把每个线程的结果相加，得到最终结果
        long total = 0L;
        for (Future<Long> f : results) {
            try {
                total += f.get();
            } catch (Exception ignore) {}
        }

        return total;
    }
}
```

如果对 `ExecutorService` 不太熟悉的话，推荐阅读[《七天七并发模型》](https://book.douban.com/subject/26337939/)的第二章，对 Java 的多线程编程基础讲解得比较清晰。当然著名的[《Java并发编程实战》](https://book.douban.com/subject/10484692/)也是不可多得的好书。

### ForkJoinPool

前面花了点时间讲解了 `ForkJoinPool` 之前的实现方法，主要为了在代码的编写难度上进行一下对比。现在就列出本篇文章的重点——`ForkJoinPool` 的实现方法。

```java
import doc1.Calculator;public class ForkJoinCalculator implements Calculator {
    private ForkJoinPool pool;

    private static class SumTask extends RecursiveTask<Long> {
        private long[] numbers;
        private int from;
        private int to;

        public SumTask(long[] numbers, int from, int to) {
            this.numbers = numbers;
            this.from = from;
            this.to = to;
        }

        @Override
        protected Long compute() {
            // 当需要计算的数字小于6时，直接计算结果
            if (to - from < 6) {
                long total = 0;
                for (int i = from; i <= to; i++) {
                    total += numbers[i];
                }
                return total;
            // 否则，把任务一分为二，递归计算
            } else {
                int middle = (from + to) / 2;
                SumTask taskLeft = new SumTask(numbers, from, middle);
                SumTask taskRight = new SumTask(numbers, middle+1, to);
                taskLeft.fork();
                taskRight.fork();
                return taskLeft.join() + taskRight.join();
            }
        }
    }

    public ForkJoinCalculator() {
        // 也可以使用公用的 ForkJoinPool：
        // pool = ForkJoinPool.commonPool()
        pool = new ForkJoinPool();
    }

    @Override
    public long sumUp(long[] numbers) {
        return pool.invoke(new SumTask(numbers, 0, numbers.length-1));
    }
}
```

可以看出，使用了 `ForkJoinPool` 的实现逻辑全部集中在了 `compute()` 这个函数里，仅用了14行就实现了完整的计算过程。特别是，在这段代码里没有显式地“把任务分配给线程”，只是分解了任务，而把具体的任务到线程的映射交给了 `ForkJoinPool` 来完成。

# 原理

如果你除了 `ForkJoinPool` 的用法以外，对 `ForkJoinPoll` 的原理也感兴趣的话，那么请接着阅读这一节。在这一节中，我会结合 `ForkJoinPool` 的作者 Doug Lea 的论文——[《A Java Fork/Join Framework》](http://gee.cs.oswego.edu/dl/papers/fj.pdf)，尽可能通俗地解释 Fork/Join Framework 的原理。

**我一直以为，要理解一样东西的原理，最好就是自己尝试着去实现一遍。**根据上面的示例代码，可以看出 `fork()` 和 `join()` 是 Fork/Join Framework “魔法”的关键。我们可以根据函数名假设一下 `fork()` 和 `join()` 的作用：

- `fork()`：开启一个新线程（或是重用线程池内的空闲线程），将任务交给该线程处理。
- `join()`：等待该任务的处理线程处理完毕，获得返回值。

以上模型似乎可以（？）解释 ForkJoinPool 能够多线程执行的事实，但有一个很明显的问题

> **当任务分解得越来越细时，所需要的线程数就会越来越多，而且大部分线程处于等待状态。**

但是如果我们在上面的示例代码加入以下代码

```java
System.out.println(pool.getPoolSize());
```

这会显示当前线程池的大小，在我的机器上这个值是4，也就是说只有4个工作线程。甚至即使我们在初始化 pool 时指定所使用的线程数为1时，上述程序也没有任何问题——除了变成了一个串行程序以外。

```java
public ForkJoinCalculator() {
    pool = new ForkJoinPool(1);
}
```

这个矛盾可以导出，**我们的假设是错误的，并不是每个 fork() 都会促成一个新线程被创建，而每个 join() 也不是一定会造成线程被阻塞。**Fork/Join Framework 的实现算法并不是那么“显然”，而是一个更加复杂的算法——这个算法的名字就叫做 *work stealing* 算法。

work stealing 算法在 Doung Lea 的[论文](http://gee.cs.oswego.edu/dl/papers/fj.pdf)中有详细的描述，以下是我在结合 Java 1.8 代码的阅读以后——现有代码的实现有一部分相比于论文中的描述发生了变化——得到的相对通俗的解释：

#### 基本思想

![img](http://blog.dyngr.com/images/20160915/forkjoinpool-structure.png)

- `ForkJoinPool` 的每个工作线程都维护着一个**工作队列**（`WorkQueue`），这是一个双端队列（Deque），里面存放的对象是**任务**（`ForkJoinTask`）。
- 每个工作线程在运行中产生新的任务（通常是因为调用了 `fork()`）时，会放入工作队列的队尾，并且工作线程在处理自己的工作队列时，使用的是 *LIFO*方式，也就是说每次从队尾取出任务来执行。
- 每个工作线程在处理自己的工作队列同时，会尝试**窃取**一个任务（或是来自于刚刚提交到 pool 的任务，或是来自于其他工作线程的工作队列），窃取的任务位于其他线程的工作队列的队首，也就是说工作线程在窃取其他工作线程的任务时，使用的是 *FIFO* 方式。
- 在遇到 `join()` 时，如果需要 join 的任务尚未完成，则会先处理其他任务，并等待其完成。
- 在既没有自己的任务，也没有可以窃取的任务时，进入休眠。

下面来介绍一下关键的两个函数：`fork()` 和 `join()` 的实现细节，相比来说 `fork()` 比 `join()` 简单很多，所以先来介绍 `fork()`。

#### fork

`fork()` 做的工作只有一件事，既是**把任务推入当前工作线程的工作队列里**。可以参看以下的源代码：

```java
public final ForkJoinTask<V> fork() {
    Thread t;
    if ((t = Thread.currentThread()) instanceof ForkJoinWorkerThread)
        ((ForkJoinWorkerThread)t).workQueue.push(this);
    else
        ForkJoinPool.common.externalPush(this);
    return this;
}
```

#### join

`join()` 的工作则复杂得多，也是 `join()` 可以使得线程免于被阻塞的原因——不像同名的 `Thread.join()`。

1. 检查调用 `join()` 的线程是否是 ForkJoinThread 线程。如果不是（例如 main 线程），则阻塞当前线程，等待任务完成。如果是，则不阻塞。
2. 查看任务的完成状态，如果已经完成，直接返回结果。
3. 如果任务尚未完成，但处于自己的工作队列内，则完成它。
4. 如果任务已经被其他的工作线程偷走，则窃取这个小偷的工作队列内的任务（以 *FIFO* 方式），执行，以期帮助它早日完成欲 join 的任务。
5. 如果偷走任务的小偷也已经把自己的任务全部做完，正在等待需要 join 的任务时，则找到小偷的小偷，帮助它完成它的任务。
6. 递归地执行第5步。

将上述流程画成序列图的话就是这个样子：

![img](http://blog.dyngr.com/images/20160915/flowchart-of-join.png)

以上就是 `fork()` 和 `join()` 的原理，这可以解释 ForkJoinPool 在递归过程中的执行逻辑，但还有一个问题

> **最初的任务是 push 到哪个线程的工作队列里的？**

这就涉及到 `submit()` 函数的实现方法了

#### submit

其实除了前面介绍过的每个工作线程自己拥有的工作队列以外，`ForkJoinPool` 自身也拥有工作队列，这些工作队列的作用是用来接收由外部线程（非 `ForkJoinThread` 线程）提交过来的任务，而这些工作队列被称为 *submitting queue* 。

`submit()` 和 `fork()` 其实没有本质区别，只是提交对象变成了 submitting queue 而已（还有一些同步，初始化的操作）。submitting queue 和其他 work queue 一样，是工作线程”窃取“的对象，因此当其中的任务被一个工作线程成功窃取时，就意味着提交的任务真正开始进入执行阶段。

# 总结

在了解了 Fork/Join Framework 的工作原理之后，相信很多使用上的注意事项就可以从原理中找到原因。例如：**为什么在 ForkJoinTask 里最好不要存在 I/O 等会阻塞线程的行为？**，这个我姑且留作思考题吧 :)

还有一些延伸阅读的内容，在此仅提及一下：

1. `ForkJoinPool` 有一个 *Async Mode* ，效果是**工作线程在处理本地任务时也使用 FIFO 顺序**。这种模式下的 `ForkJoinPool` 更接近于是一个消息队列，而不是用来处理递归式的任务。
2. 在需要阻塞工作线程时，可以使用 `ManagedBlocker`。
3. Java 1.8 新增加的 `CompletableFuture` 类可以实现类似于 Javascript 的 promise-chain，内部就是使用 `ForkJoinPool` 来实现的。

#### 彩蛋

之所以煞有介事地取名为 **POFLC**，显然是为了模仿 **POJO** 。而 **POJO** —— *Plain Old Java Object* 这个词是如何产生的，在 stackoverflow 上有个[帖子](http://stackoverflow.com/questions/3326319/what-does-the-term-plain-old-java-object-pojo-exactly-mean)讨论过，摘录一下就是

> I’ve come to the conclusion that people forget about regular Java objects because they haven’t got a fancy name. That’s why, while preparing for a talk in 2000, Rebecca Parsons, Josh Mackenzie, and I gave them one: POJOs (plain old Java objects).
>
> 我得出一个结论：人们之所以总是忘记使用标准的 Java 对象是因为缺少一个足够装逼的名字（译注：类似于 Java Bean 这样的名字）。因此，在准备2000年的演讲时，Rebecca Parsons，Josh Mackenzie 和我给他们起了一个名字叫做 POJO （平淡无奇的 Java 对象）。

