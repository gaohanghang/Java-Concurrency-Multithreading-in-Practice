package doc1;

import java.util.stream.LongStream;

/**
 * @Description
 * @Author Gao Hang Hang
 * @Date 2019-07-24 20:28
 **/
public class Main {
    public static void main(String[] args) {
        long[] numbers = LongStream.rangeClosed(1, 1000).toArray();
        Calculator calculator = new ForkJoinCalculator();
        System.out.println(calculator.sumUp(numbers)); // 打印结果500500
    }
}
