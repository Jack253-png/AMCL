package com.mcreater.amcl.util.concurrent;

public class Sleeper {
    public static void sleep(long millis) {
        try{
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
