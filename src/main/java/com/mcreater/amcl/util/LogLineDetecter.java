package com.mcreater.amcl.util;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.Charset;

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
            else if (s.contains("/ERROR") || s.startsWith("\tat") || s.matches(".: .")) printLogInternal(s, ERROR, stream);
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
    public static String readStreamContent(InputStream inputStream) {
        StringBuilder f = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                f.append(line).append("\n");
            }
        } catch (IOException ignored) {

        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return f.toString();
    }
    public static void printStreamToPrintStream(InputStream stream, PrintStream out){
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, Charset.forName("GBK")));
            String line;
            while ((line = reader.readLine()) != null) {
                LogLineDetecter.printLog(line, out);
            }
        } catch (IOException ignored) {

        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
