package com.mcreater.amcl.util.concurrent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.io.PrintStream;

public class StdOutErrRedirect extends PrintStream{
    private final static Logger logger = LogManager.getLogger(StdOutErrRedirect.class);

    public StdOutErrRedirect(@NotNull OutputStream out) {
        super(out);
    }
    public static void redirectSystemOutAndErrToLog() {
        logger.info("redirecting");
        PrintStream printStreamForOut = new StdOutErrRedirect(System.out);
        System.setOut(printStreamForOut);
    }
    public void print(Object obj){logger.info(obj);}
    public void print(int i){logger.info(i);}
    public void print(long l){logger.info(l);}
    public void print(char c){logger.info(c);}
    public void print(String s){logger.info(s);}
    public void print(float f){logger.info(f);}
    public void print(double d){logger.info(d);}
    public void print(boolean b){logger.info(b);}
    public void println(Object obj){print(obj);}
    public void println(int obj){print(obj);}
    public void println(long obj){print(obj);}
    public void println(char obj){print(obj);}
    public void println(String obj){print(obj);}
    public void println(float obj){print(obj);}
    public void println(double obj){print(obj);}
    public void println(boolean obj){print(obj);}
    public PrintStream printf(String s, Object... objs){
        logger.info(String.format(s, objs));
        return super.format(s, objs);
    }
}
