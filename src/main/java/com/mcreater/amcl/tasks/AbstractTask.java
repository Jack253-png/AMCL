package com.mcreater.amcl.tasks;

import java.io.IOException;

public abstract class AbstractTask implements Task {
    public String server;
    public String local;
    String command;
    Runnable runnable;
    public AbstractTask(String command){
        this.command = command;
    }
    public AbstractTask(String server, String local){
        this.server = server;
        this.local = local;
    }
    public AbstractTask(Runnable runnable){
        this.runnable = runnable;
    }

    public boolean isFinished() {
        return true;
    }
    public abstract Integer execute() throws IOException;
    public String toString(){
        return this.server;
    }
}
