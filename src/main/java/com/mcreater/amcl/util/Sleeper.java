package com.mcreater.amcl.util;

public class Sleeper {
    public static void sleep(long millis){
        try{
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
