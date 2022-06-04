package com.mcreater.amcl.download.tasks;

import java.io.IOException;

public abstract class AbstractTask {
    public String server;
    String local;
    public AbstractTask(String server, String local){
        this.server = server;
        this.local = local;
    }
    public abstract void execute() throws IOException;
}
