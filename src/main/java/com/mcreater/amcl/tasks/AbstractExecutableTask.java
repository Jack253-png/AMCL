package com.mcreater.amcl.tasks;

import java.io.IOException;

public abstract class AbstractExecutableTask implements Task {
    public AbstractExecutableTask() {}
    public abstract Integer execute() throws IOException;
}
