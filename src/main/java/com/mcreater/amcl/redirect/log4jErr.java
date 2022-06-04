package com.mcreater.amcl.redirect;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;

public class log4jErr extends PrintStream {
    private final Logger logger = LogManager.getLogger("STDERR");
    private static final PrintStream instance = new log4jErr(System.err);
    private log4jErr(PrintStream printStream){
        super(printStream);
    }
    public static void redirect(){
        System.setOut(instance);
    }
    public void println(Object o){logger.error(o);}
    public void println(char c){logger.error(c);}
    public void println(double d){logger.error(d);}
    public void println(float f){logger.error(f);}
    public void println(int i){logger.error(i);}
    public void println(long l){logger.error(l);}
    public void println(String s){logger.error(s);}
}
