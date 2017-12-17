package csci3310gp10.cusocmanager;

import java.util.List;

/**
 * Created by KaHei on 18/12/2017.
 */

public interface RequestTaskResult<T extends Object> {
    public void taskFinish(T result);
}
