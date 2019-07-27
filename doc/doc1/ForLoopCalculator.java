package doc1;

/**
 * @Description
 * @Author Gao Hang Hang
 * @Date 2019-07-24 20:28
 **/
public class ForLoopCalculator implements Calculator {
    public long sumUp(long[] numbers) {
        long total = 0;
        for (long i : numbers) {
            total += i;
        }
        return total;
    }
}
