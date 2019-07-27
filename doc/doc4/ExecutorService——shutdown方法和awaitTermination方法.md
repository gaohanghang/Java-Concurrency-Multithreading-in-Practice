# ExecutorService——shutdown方法和awaitTermination方法

## ExecutorService的关闭

shutdown和awaitTermination为接口ExecutorService定义的两个方法，一般情况配合使用来关闭线程池。

## 方法简介

1. shutdown方法：平滑的关闭ExecutorService，当此方法被调用时，ExecutorService停止接收新的任务并且等待已经提交的任务（包含提交正在执行和提交未执行）执行完成。当所有提交任务执行完毕，线程池即被关闭。
2. awaitTermination方法：接收人timeout和TimeUnit两个参数，用于设定超时时间及单位。当等待超过设定时间时，会监测ExecutorService是否已经关闭，若关闭则返回true，否则返回false。一般情况下会和shutdown方法组合使用。

## 具体实例

普通任务处理类：

```java
package com.secbro.test.thread;

import java.util.concurrent.Callable;

/**
 * @author zhuzhisheng
 * @Description
 * @date on 2016/6/1.
 */
public class Task implements Callable{
    @Override
    public Object call() throws Exception {
        System.out.println("普通任务");
        return null;
    }
}
```

长时间任务处理类：

```java
package com.secbro.test.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author zhuzhisheng
 * @Description
 * @date on 2016/6/1.
 */
public class LongTask implements Callable{
    @Override
    public Object call() throws Exception {
        System.out.println("长时间任务");
        TimeUnit.SECONDS.sleep(5);
        return null;
    }
}
```

测试类：

```java
package com.secbro.test.thread;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author zhuzhisheng
 * @Description
 * @date on 2016/6/1.
 */
public class TestShutDown {

    public static void main(String[] args) throws InterruptedException{
        ScheduledExecutorService service = Executors.newScheduledThreadPool(4);

        service.submit(new Task());
        service.submit(new Task());
        service.submit(new LongTask());
        service.submit(new Task());


        service.shutdown();

        while (!service.awaitTermination(1, TimeUnit.SECONDS)) {
            System.out.println("线程池没有关闭");
        }

        System.out.println("线程池已经关闭");
    }

}
```

输出结果为：

```
普通任务
普通任务
长时间任务
普通任务
线程池没有关闭
线程池没有关闭
线程池没有关闭
线程池没有关闭
线程池已经关闭
```

