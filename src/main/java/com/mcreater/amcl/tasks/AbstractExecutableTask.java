package com.mcreater.amcl.tasks;

import java.io.IOException;

public abstract class AbstractExecutableTask implements Task {
    String command;
    Runnable runnable;
    public AbstractExecutableTask(String command){
        this.command = command;
    }
    public AbstractExecutableTask(Runnable runnable){
        this.runnable = runnable;
    }
    public abstract Integer execute() throws IOException;
}
