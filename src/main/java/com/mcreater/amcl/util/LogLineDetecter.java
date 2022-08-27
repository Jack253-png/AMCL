package com.mcreater.amcl.util;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import java.io.PrintStream;

import static org.fusesource.jansi.Ansi.ansi;

public class LogLineDetecter {
    public static final Ansi.Color TRACE = Ansi.Color.BLUE;
    public static final Ansi.Color DEBUG = Ansi.Color.CYAN;
    public static final Ansi.Color INFO = Ansi.Color.GREEN;
    public static final Ansi.Color WARN = Ansi.Color.YELLOW;
    public static final Ansi.Color ERROR = Ansi.Color.RED;
    public static final Ansi.Color FATAL = Ansi.Color.RED;
    public static void printLog(String s, PrintStream stream){
        if (stream == System.err){
            if (s.contains("/FATAL")) printLogInternal(s, FATAL, stream);
            else if (s.contains("/ERROR")) printLogInternal(s, ERROR, stream);
            else printLogInternal(s, WARN, stream);
        }
        else {
            if (s.contains("/TRACE")) printLogInternal(s, TRACE, stream);
            else if (s.contains("/DEBUG")) printLogInternal(s, DEBUG, stream);
            else if (s.contains("/INFO")) printLogInternal(s, INFO, stream);
            else if (s.contains("/WARN")) printLogInternal(s, WARN, stream);
            else if (s.contains("/ERROR")) printLogInternal(s, ERROR, stream);
            else if (s.contains("/FATAL")) printLogInternal(s, FATAL, stream);
            else printLogInternal(s, INFO, stream);
        }
    }
    public static void printLogInLine(String s){
        System.out.print(ansi().fg(INFO).a(s).reset());
    }
    private static void printLogInternal(String s, Ansi.Color color, PrintStream stream){
        AnsiConsole.systemInstall();
        stream.println(ansi().fg(color).a(s).reset());
        AnsiConsole.systemUninstall();
    }
}
