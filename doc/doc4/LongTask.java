package doc4;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author Gao Hang Hang
 * @Date 2019-07-25 20:47
 **/
public class LongTask implements Callable {

    @Override
    public Object call() throws Exception {
        System.out.println("长时间任务");
        TimeUnit.SECONDS.sleep(5);
        return null;
    }
}
