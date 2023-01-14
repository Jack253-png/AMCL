package com.mcreater.amcl.tasks;

import java.io.IOException;

public abstract class AbstractDownloadTask implements Task {
    public String server;
    public String local;
    public TaskType type = TaskType.ORIGINAL;
    public enum TaskType {
        ORIGINAL,
        FORGE,
        FABRIC,
        LITELOADER,
        AUTHLIB_INJECTOR
    }
    public void setType (TaskType type) {
        this.type = type;
    }
    public AbstractDownloadTask(String server, String local){
        this.server = server;
        this.local = local;
    }
    public abstract Integer execute() throws IOException;
    public String toString() {
        return String.format("%s task from %s to %s", this.getClass().getSimpleName(), server, local);
    }
}
