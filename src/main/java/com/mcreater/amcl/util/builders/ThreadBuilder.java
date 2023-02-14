package com.mcreater.amcl.util.builders;

public class ThreadBuilder {
    public enum ThreadPriority {
        MIN(Thread.MIN_PRIORITY),
        NORMAL(Thread.NORM_PRIORITY),
        MAX(Thread.MAX_PRIORITY);
        private final int priority;
        ThreadPriority(int pri) {
            this.priority = pri;
        }

        public int getPriority() {
            return priority;
        }
    }
    private Thread thread;
    private ThreadBuilder() {}
    public static ThreadBuilder createBuilder() {
        return new ThreadBuilder();
    }

    public ThreadBuilder runTarget(Runnable runnable) {
        thread = new Thread(runnable);
        return this;
    }

    public ThreadBuilder name(String name) {
        thread.setName(name);
        return this;
    }

    public ThreadBuilder priority(ThreadPriority priority) {
        thread.setPriority(priority.getPriority());
        return this;
    }

    public ThreadBuilder daemon(boolean isDaemon) {
        thread.setDaemon(isDaemon);
        return this;
    }

    public ThreadBuilder contextClassLoader(ClassLoader classLoader) {
        thread.setContextClassLoader(classLoader);
        return this;
    }

    public ThreadBuilder handler(Thread.UncaughtExceptionHandler handler) {
        thread.setUncaughtExceptionHandler(handler);
        return this;
    }

    public Thread build() {
        return thread;
    }

    public Thread buildAndRun() {
        thread.start();
        return build();
    }
}
