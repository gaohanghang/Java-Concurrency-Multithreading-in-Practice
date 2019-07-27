# Java-Concurrency-Multithreading-in-Practice

> 视频地址  https://subscription.packtpub.com/video/programming/9781789806410

## section1 并行运行任务

### 课程概述

### 使用ForkJoinPool并行执行任务

### 将任务结果合并 Joining the Results of the Tasks

Take our first parallel task one step further and combine results of individual subtasks into a single final result.
将我们的第一个并行任务更进一步，将单个子任务的结果合并为一个最终结果。

### RecursiveAction和RecursiveTask

创建ForkJoinPool实例后，可以钓鱼ForkJoinPool的submit(ForkJoinTask<T> task)或者invoke(ForkJoinTask<T> task)来执行指定任务。其中ForkJoinTask代表一个可以并行、合并的任务。ForkJoinTask是一个抽象类，它有两个抽象子类：RecursiveAction和RecursiveTask。

- RecursiveTask代表有返回值的任务
- RecursiveAction代表没有返回值的任务。

### 异常处理和取消任务

## section2 Executing Functions in Parallel with Parallel Streams and Futures

### Callable and Future

> Callable和Future是异步计算的核心抽象。它们代表任务及其未来结果，并为几乎所有其他多线程抽象创建基础。

![](https://raw.githubusercontent.com/gaohanghang/images/master/img20190725222112.png)

![](https://raw.githubusercontent.com/gaohanghang/images/master/img20190725222143.png)

Callable的任务执行后可返回值，而Runnable的任务是不能返回值(是void)

### Thread and Thread Pool

> 线程是多线程的根源。这将是我们第一次遇到线程的概念。

![](https://raw.githubusercontent.com/gaohanghang/images/master/img20190725222750.png)

### CompletableFuture
> CompletableFuture将Future带到了一个新的水平，并允许我们在不同的任务之间创建依赖关系。例如，在其他任务/阶段成功完成时触发一个任务/阶段的执行。



## section5 线程安全数据结构

### 原子类型和CAS Atomic Types and the Compare-and-Set Approach

> 从头开始为变量提供线程安全性是乏味，乏味和容易出错的。将线程安全性委托给已经存在且经过良好测试的数据结构，使我们能够在开发和测试期间节省大量时间。

### 并发集合：ConcurrentHashMap，ConcurrentLinkedQueue

> HashMap和LinkedList可能是Java中使用最广泛的集合，这就是为什么使用这些集合的高效线程安全实现很重要的原因。

### 阻塞队列

> 消费者 - 生产者问题在并发编程中很常见。幸运的是，Java为这一普遍问题提供了一系列高效的数据结构。

### Copy-on-Write Collections

![](https://raw.githubusercontent.com/gaohanghang/images/master/img20190725210917.png)

### 总结和后续步骤

![](https://raw.githubusercontent.com/gaohanghang/images/master/img20190725211106.png)

![](https://raw.githubusercontent.com/gaohanghang/images/master/img20190725211311.png)

Parallel stream 并行流

Reactive stream 响应流





