package com.mcreater.amcl.tasks;

import java.io.IOException;

public class LambdaTask extends AbstractTask{
    public LambdaTask(Runnable runnable) {
        super(runnable);
    }
    public Integer execute() throws IOException {
        runnable.run();
        return null;
    }
}
