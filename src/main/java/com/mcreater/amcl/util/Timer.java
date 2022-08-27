package com.mcreater.amcl.util;

import java.text.DecimalFormat;

public abstract class Timer {
    public long current;
    public Timer(){
        current = System.currentTimeMillis();
    }
    public Timer(long current){
        this.current = current;
    }

    protected abstract long getTimeMillis();
    public abstract String getTimeString();
    public static Timer getInstance(){
        return new Timer() {
            protected long getTimeMillis() {
                return System.currentTimeMillis() - current;
            }

            public String getTimeString() {
                long time = getTimeMillis();
                DecimalFormat df = new DecimalFormat("0.00");
                current = System.currentTimeMillis();
                if (time < 60 * 1000){
                    return df.format(g(time, 1000)) + " s";
                }
                else if (time < 60 * 60 * 1000){
                    return df.format(g(time, 60 * 1000)) + " m";
                }
                else {
                    return df.format(g(time, 60 * 60 * 1000)) + " h";
                }
            }
        };

    }
    public static double g(long time, int data){
        return ((double) time) / data;
    }
    public static double g(long a, long b){
        return ((double) a) / b;
    }
}
