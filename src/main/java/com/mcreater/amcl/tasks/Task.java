package com.mcreater.amcl.tasks;

import com.mcreater.amcl.util.concurrent.PoolName;
import java.io.IOException;

public interface Task {
    String DEFAULT_POOL = "<default>";
    PoolName pool = new PoolName(Task.DEFAULT_POOL);
    Integer execute() throws IOException;
}
