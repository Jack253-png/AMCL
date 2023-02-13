package com.mcreater.amcl.util;

import java.text.DecimalFormat;
import java.util.function.Supplier;

public abstract class Timer {
    public long current;

    private Timer() {
        current = System.currentTimeMillis();
    }

    private Timer(long current) {
        this.current = current;
    }

    protected abstract long getTimeMillis();

    public abstract String getTimeString();

    public static Timer getInstance() {
        return new Timer() {
            protected long getTimeMillis() {
                return System.currentTimeMillis() - current;
            }

            public String getTimeString() {
                long time = getTimeMillis();
                DecimalFormat df = new DecimalFormat("0.00");
                current = System.currentTimeMillis();
                if (time < 60 * 1000) {
                    return df.format(division(time, 1000)) + " s";
                } else if (time < 60 * 60 * 1000) {
                    return df.format(division(time, 60 * 1000)) + " m";
                } else {
                    return df.format(division(time, 60 * 60 * 1000)) + " h";
                }
            }
        };

    }

    public void reset() {
        current = System.currentTimeMillis();
    }

    public static double division(long time, int data) {
        return ((double) time) / data;
    }

    public static double division(long a, long b) {
        return ((double) a) / b;
    }

    public static <T> T runCheck(Supplier<T> supplier) {
        Timer timer = Timer.getInstance();
        T t = supplier.get();
        System.out.println(timer.getTimeString());
        return t;
    }
}
