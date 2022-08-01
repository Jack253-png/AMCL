package com.mcreater.amcl.util.concurrent;

import com.mcreater.amcl.tasks.Task;

public class PoolName {
    public String name = Task.DEFAULT_POOL;
    public PoolName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }
}
