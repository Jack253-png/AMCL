package com.mcreater.amcl.util;

public class SimpleFunctions {
    public interface Arg0FuncNoReturn {
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
}

