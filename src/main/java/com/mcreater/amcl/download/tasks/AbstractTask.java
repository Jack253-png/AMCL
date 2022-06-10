package com.mcreater.amcl.download.tasks;

import java.io.IOException;

public abstract class AbstractTask {
    public String server;
    String local;
    String command;
    public AbstractTask(String command){
        this.command = command;
    }
    public AbstractTask(String server, String local){
        this.server = server;
        this.local = local;
    }
    public abstract Integer execute() throws IOException;
}
