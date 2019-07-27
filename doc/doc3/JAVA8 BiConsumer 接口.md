# JAVA8 BiConsumer 接口

> 原文地址

这个接口跟[《JAVA8 Consumer接口》](https://blog.csdn.net/qq_28410283/article/details/80618456)很像，表达的想法也是一致的，都是消费的意思，我们先看下接口定义

```java
@FunctionalInterface
public interface BiConsumer<T, U> {
 
    
    void accept(T t, U u);
 
	/**本接口中的accept先执行，传入的BiConsumer 接口类型的参数，后执行accept*/
    default BiConsumer<T, U> andThen(BiConsumer<? super T, ? super U> after) {
        Objects.requireNonNull(after);
 
        return (l, r) -> {
            accept(l, r);
            after.accept(l, r);
        };
    }
}
```

这个接口接收两个泛型参数，跟Consumer一样，都有一个 accept方法，只不过，这里的，接收两个泛型参数，对这两个参数做下消费处理；使用这个函数式接口的终端操作有map的遍历；下面看下面的例子，两个参数消费数据的

```java
 Map<String, String> map = new HashMap<>();
        map.put("a", "a");
        map.put("b", "b");
        map.put("c", "c");
        map.put("d", "d");
        map.forEach((k, v) -> {
            System.out.println(k);
            System.out.println(v);
        });
```

可以看到，Map接口的终端操作，forEach的参数就是BiConsumer函数接口，对HashMap 的数据进行消费；BiConsumer函数接口还有一个默认函数，andThen，接收一个BiConsumer接口，先执行本接口的，再执行传入的参数。

