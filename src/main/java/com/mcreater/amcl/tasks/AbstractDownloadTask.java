package com.mcreater.amcl.tasks;

import java.io.IOException;

public abstract class AbstractDownloadTask implements Task {
    public String server;
    public String local;
    String command;
    Runnable runnable;
    public AbstractDownloadTask(String command){
        this.command = command;
    }
    public AbstractDownloadTask(String server, String local){
        this.server = server;
        this.local = local;
    }
    public AbstractDownloadTask(Runnable runnable){
        this.runnable = runnable;
    }
    public abstract Integer execute() throws IOException;
}
