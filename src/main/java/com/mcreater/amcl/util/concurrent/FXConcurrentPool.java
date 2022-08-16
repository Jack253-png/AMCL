package com.mcreater.amcl.util.concurrent;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class FXConcurrentPool {
    public static Service<String> run(Runnable runnable){
        return new Service<String>() {
            @Override
            protected Task<String> createTask() {
                return new Task<String>() {
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
