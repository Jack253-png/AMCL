package com.mcreater.amcl.tasks;

import java.util.Vector;

public abstract class AbstractCommandTask extends AbstractExecutableTask {
    Vector<String> command;
    public AbstractCommandTask(Vector<String> command){
        this.command = command;
    }
}
