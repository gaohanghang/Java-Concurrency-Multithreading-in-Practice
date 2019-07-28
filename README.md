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

### 顺序流 Sequential Streams 

> 当以函数式编写时，许多对集合的操作通信更好。
> Java Streams允许我们在集合上使用函数式，使代码更具可读性，因此更易于维护。

![](https://raw.githubusercontent.com/gaohanghang/images/master/img20190727155545.png)

### 并行流 Parallel Streams

![](https://raw.githubusercontent.com/gaohanghang/images/master/img20190727160425.png)

![](https://raw.githubusercontent.com/gaohanghang/images/master/img20190727160458.png)

## section3 Reactive Streams 

### lesson1 概述：发布者，订阅者和订阅

### lesson2 订阅者：消费消息 

> 订阅者是Flow API的主要组件之一。它处理发布者发送的数据。

### Publisher和SubmissionPublisher：提供消息

> Publisher是Flow API的另一个主要组件。我们将学习几种实现方法。

![](https://raw.githubusercontent.com/gaohanghang/images/master/img20190727210112.png)

### 处理器：转换消息

> 处理器是一个可选元素，用于转换数据项，以及发布者和其他订阅者之间的行为和中介。

![](https://raw.githubusercontent.com/gaohanghang/images/master/img20190727210705.png)

## Section4 线程和锁

### 线程：线程生命周期

> 线程是并发的基本抽象。了解它的工作原理对于编写并发程序至关重要。

![](https://raw.githubusercontent.com/gaohanghang/images/master/img20190728121244.png)

### 结构化锁（“同步”关键字）Structured Locks ("synchronized" Keyword)

> Threads share data which provide a lot of opportunities, but also create challenges. The synchronized statements and methods address it by ensuring mutually exclusive access.
> 线程共享提供大量机会的数据，但也带来了挑战。synchronized语句和方法通过确保互斥访问来解决它。

![](https://raw.githubusercontent.com/gaohanghang/images/master/img20190728121406.png)

### 使用锁

> 同步块很有用，但它们缺乏灵活性。例如，它们需要锁定获取和释放以块结构方式发生。与简单的“同步”语句相比，更新的Lock接口提供了更灵活和复杂的线程同步机制。

![](https://raw.githubusercontent.com/gaohanghang/images/master/img20190728121858.png)

![](https://raw.githubusercontent.com/gaohanghang/images/master/img20190728121933.png)

### Object#wait(), notify(), and the New onSpinWait()

> 有时，应该在某个事件上激活一个线程。在这种情况下，wait（）和notify（）可以很方便地协调线程。

### 可能的问题：数据竞争，死锁，活锁和资源饥饿

> 使用并发线程可能具有挑战性，并且可能会产生新的错误类。了解它们是防止它们的第一步。

死锁

我们举个例子来描述，如果此时有一个线程A，按照先锁a再获得锁b的的顺序获得锁，而在此同时又有另外一个线程B，按照先锁b再锁a的顺序获得锁。如下图所示：

![](https://raw.githubusercontent.com/gaohanghang/images/master/img20190728123017.png)

![](https://raw.githubusercontent.com/gaohanghang/images/master/img20190728123122.png)

Summary

![](https://raw.githubusercontent.com/gaohanghang/images/master/img20190728123237.png)

Summary(Continued)

- Discussed: 
    - Problems:
        - Data races 数据抢占
        - Race conditions 比赛条件
        - Liveness 活跃度

![](https://raw.githubusercontent.com/gaohanghang/images/master/img20190728123334.png)

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





