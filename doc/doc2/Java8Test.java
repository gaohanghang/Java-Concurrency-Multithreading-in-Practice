package doc2;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.LongStream;


/**
 * @Description 并行流 与 顺序流
 * @Author Gao Hang Hang
 * @Date 2019-07-24 23:40
 **/
public class Java8Test {

    public static void main(String[] args) {
        Instant start = Instant.now();
        LongStream.rangeClosed(0, 110)
                //并行流
                .parallel()
                .reduce(0, Long::sum);

        LongStream.rangeClosed(0, 110)
                //顺序流
                .sequential()
                .reduce(0, Long::sum);

        Instant end = Instant.now();
        System.out.println("耗费时间" + Duration.between(start, end).toMillis());
    }
}
