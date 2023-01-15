package com.mcreater.amcl.tasks;

import java.io.IOException;

public class LambdaTask extends AbstractExecutableTask {
    private final Runnable runnable;
    public LambdaTask(Runnable runnable) {
        this.runnable = runnable;
    }
    public Integer execute() throws IOException {
        runnable.run();
        return null;
    }
}
