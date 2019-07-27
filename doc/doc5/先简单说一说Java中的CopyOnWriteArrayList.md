# 先简单说一说Java中的CopyOnWriteArrayList

### 1、`Copy-On-Write` 是什么？

首先我讲一下什么是`Copy-On-Write`，顾名思义，在计算机中就是当你想要对一块内存进行修改时，我们不在原有内存块中进行`写`操作，而是将内存拷贝一份，在新的内存中进行`写`操作，`写`完之后呢，就将指向原来内存指针指向新的内存，原来的内存就可以被回收掉嘛！

网上兄弟们说了，这是一种用于程序设计中的`优化策略`，是一种`延时懒惰策略`。都说优化优化，那么到底优化了哪些问题呢？

先给大家一份代码：

```
public class IteratorTest {

	private static List<String> list = new ArrayList<>();

	public static void main(String[] args) {
		
		list.add("1");
		list.add("2");
		list.add("3");
		
		Iterator<String> iter = list.iterator();
		
		//我当前正在迭代集合（这里模拟并发中读取某一list的场景）
		while (iter.hasNext()) {
			
			System.err.println(iter.next());
		
		}
		
		System.err.println(Arrays.toString(list.toArray()));
	}
}
复制代码
```

上面的程序片段在单线程下执行时没什么毛病的，但到了多线程的环境中，可能就GG了！为什么呢？因为多线程环境中，你在迭代的时候是不允许有其他线程对这个集合list进行添加元素的，看下面这段代码，你会发现抛出`java.util.ConcurrentModificationException`的异常。

```
public class IteratorTest {

	private static List<String> list = new ArrayList<>();

	public static void main(String[] args) {

		list.add("1");
		list.add("2");
		list.add("3");

		Iterator<String> iter = list.iterator();

		// 存放10个线程的线程池
		ExecutorService service = Executors.newFixedThreadPool(10);

		// 执行10个任务(我当前正在迭代集合（这里模拟并发中读取某一list的场景）)
		for (int i = 0; i < 10; i++) {
			service.execute(new Runnable() {
				@Override
				public void run() {
					while (iter.hasNext()) {
						System.err.println(iter.next());
					}
				}
			});
		}
		
		// 执行10个任务
		for (int i = 0; i < 10; i++) {
			service.execute(new Runnable() {
				@Override
				public void run() {
					list.add("121");// 添加数据
				}
			});
		}
		
		System.err.println(Arrays.toString(list.toArray()));
		
	}
}
复制代码
```

- 1、这里的`迭代`表示我当前正在读取某种`集合`中的数据,属于`读`操作；
- 2、线程则模拟当前程序处于多线程环境中，有其他线程正在修改该数据

这里暴露的问题是什么呢？

- 1、多线程会对迭代集合产生影响，影响读操作

解决：

- 1、`CopyOnWriteArrayList` 避免了多线程操作List线程不安全的问题

### 2、`CopyOnWriteArrayList`介绍

从JDK1.5开始Java并发包里提供了两个使用`CopyOnWrite`机制实现的并发容器,它们是`CopyOnWriteArrayList`和`CopyOnWriteArraySet`。`CopyOnWrite`容器非常有用，可以在非常多的并发场景中使用到。

`CopyOnWriteArrayList`原理：

```
上面已经讲了，就是在写的时候不对原集合进行修改，而是重新复制一份，修改完之后，再移动指针
复制代码
```

那么你可能会问？就算是对原集合进行复制，在多线程环境中不也是一样会导致写入冲突吗？没错，但是你可能还不知道`CopyOnWriteArrayList`中增加删除元素的实现细节，下面我就说说网上老是提到的`add()方法`

### 3、`CopyOnWriteArrayList`简单源码解读

`add()`方法源码：

```
/**
     * Appends the specified element to the end of this list.
     *
     * @param e element to be appended to this list
     * @return {@code true} (as specified by {@link Collection#add})
     */
    public boolean add(E e) {
        final ReentrantLock lock = this.lock;//重入锁
        lock.lock();//加锁啦
        try {
            Object[] elements = getArray();
            int len = elements.length;
            Object[] newElements = Arrays.copyOf(elements, len + 1);//拷贝新数组
            newElements[len] = e;
            setArray(newElements);//将引用指向新数组  1
            return true;
        } finally {
            lock.unlock();//解锁啦
        }
    }
复制代码
```

恍然大悟，小样，原来`add()`在添加集合的时候加上了锁，保证了同步，避免了多线程写的时候会Copy出N个副本出来。(`想想，你在遍历一个10个元素的集合，每遍历一次有1人调用add方法，你说当你遍历10次，这add方法是不是得被调用10次呢？是不是得copy出10分新集合呢？万一这个集合非常大呢？`)

那么？你还要问？`CopyOnWriteArrayList`是怎么解决线程安全问题的？答案就是----`写时复制，加锁` 还要问？那么有没有这么一种情况，当一个线程刚好调用完`add()`方法，也就是刚好执行到上面`1`处的代码，也就是刚好将引用指向心数组，而此时有线程正在遍历呢？会不会报错呢？（`答案是不会的，因为你正在遍历的集合是旧的，这就有点难受啦，哈哈~`）

当你把上面的代码的`ArrayList`改为`CopyOnWriteArrayList`，执行就不会报错啦！

```
public class IteratorTest {

	private static CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();

	public static void main(String[] args) {

		list.add("1");
		list.add("2");
		list.add("3");

		Iterator<String> iter = list.iterator();

		// 存放10个线程的线程池
		ExecutorService service = Executors.newFixedThreadPool(10);

		// 执行10个任务(我当前正在迭代集合（这里模拟并发中读取某一list的场景）)
		for (int i = 0; i < 10; i++) {
			service.execute(new Runnable() {
				@Override
				public void run() {
					while (iter.hasNext()) {
						System.err.println(iter.next());
					}
				}
			});
			service.execute(new Runnable() {
				@Override
				public void run() {
					list.add("121");// 添加数据
				}
			});
		}
		
		// 执行10个任务
		for (int i = 0; i < 10; i++) {
			service.execute(new Runnable() {
				@Override
				public void run() {
					list.add("121");// 添加数据
				}
			});
			service.execute(new Runnable() {
				@Override
				public void run() {
					while (iter.hasNext()) {
						System.err.println(iter.next());
					}
				}
			});
		}
		
		System.err.println(Arrays.toString(list.toArray()));
		
	}
}
复制代码
```

### 4、`CopyOnWriteArrayList`优缺点

缺点：

- 1、耗内存（集合复制）
- 2、实时性不高

优点：

- 1、数据一致性完整，为什么？因为加锁了，并发数据不会乱
- 2、解决了`像ArrayList`、`Vector`这种集合多线程遍历迭代问题，记住，`Vector`虽然线程安全，只不过是加了`synchronized`关键字，迭代问题完全没有解决！

### 5、`CopyOnWriteArrayList`使用场景

- 1、读多写少（白名单，黑名单，商品类目的访问和更新场景），为什么？因为写的时候会复制新集合
- 2、集合不大，为什么？因为写的时候会复制新集合
- 实时性要求不高，为什么，因为有可能会读取到旧的集合数据

参考文章：[如何线程安全地遍历List：Vector、CopyOnWriteArrayList](https://link.juejin.im/?target=https%3A%2F%2Fwww.cnblogs.com%2Fwucao%2Fp%2F5350461.html)

