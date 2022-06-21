package com.mcreater.amcl.download.tasks;

import com.sun.javafx.tk.Toolkit;

import java.io.IOException;

public abstract class AbstractTask implements Toolkit.Task {
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

    public boolean isFinished() {
        return true;
    }

    public abstract Integer execute() throws IOException;
}
