package doc4;

import java.util.concurrent.Callable;

/**
 * @Description
 * @Author Gao Hang Hang
 * @Date 2019-07-25 20:46
 **/
public class Task implements Callable {

    @Override
    public Object call() throws Exception {
        System.out.println("普通任务");
        return null;
    }

}
