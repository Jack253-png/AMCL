package com.mcreater.amcl.util;

public class SimpleFunctions {
    public interface Arg0FuncNoReturn extends Runnable {
        void run();
    }
    public interface Arg0Func<return_type> {
        return_type run();
    }
    public interface Arg1FuncNoReturn<arg1_type> {
        void run(arg1_type arg1);
    }
    public interface Arg1Func<arg1_type, return_type> {
        return_type run(arg1_type arg1);
    }
    public interface Arg2FuncNoReturn<arg1_type, arg2_type> {
        void run(arg1_type arg1, arg2_type arg2);
    }
}

