package com.mcreater.amcl.tasks;

import com.mcreater.amcl.util.PoolName;
import com.sun.javafx.tk.Toolkit;

import java.io.IOException;

public interface Task extends Toolkit.Task {
    String DEFAULT_POOL = "<default>";
    PoolName pool = new PoolName(Task.DEFAULT_POOL);
    boolean isFinished();
    Integer execute() throws IOException;
}
