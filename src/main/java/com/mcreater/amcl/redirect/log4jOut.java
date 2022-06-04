package com.mcreater.amcl.redirect;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;

public class log4jOut extends PrintStream {
    private final Logger logger = LogManager.getLogger("STDOUT");
    private static final PrintStream instance = new log4jOut(System.out);
    private log4jOut(PrintStream printStream){
        super(printStream);
    }
    public static void redirect(){
        System.setOut(instance);
    }
    public void println(Object o){logger.info(o);}
    public void println(char c){logger.info(c);}
    public void println(double d){logger.info(d);}
    public void println(float f){logger.info(f);}
    public void println(int i){logger.info(i);}
    public void println(long l){logger.info(l);}
    public void println(String s){logger.info(s);}
}
