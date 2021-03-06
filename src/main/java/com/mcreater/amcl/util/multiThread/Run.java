package com.mcreater.amcl.util.multiThread;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class Run {
    public static Service<String> run(Runnable runnable){
        return new Service<>() {
            @Override
            protected Task<String> createTask() {
                return new Task<>() {
                    @Override
                    protected String call() {
                        runnable.run();
                        return null;
                    }
                };
            }
        };
    }
}
