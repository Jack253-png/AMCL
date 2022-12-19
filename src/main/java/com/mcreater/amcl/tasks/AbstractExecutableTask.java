package com.mcreater.amcl.tasks;

import java.io.IOException;
import java.util.Vector;

public abstract class AbstractExecutableTask implements Task {
    Vector<String> command;
    Runnable runnable;
    public AbstractExecutableTask(Vector<String> command){
        this.command = command;
    }
    public AbstractExecutableTask(Runnable runnable){
        this.runnable = runnable;
    }
    public abstract Integer execute() throws IOException;
}
