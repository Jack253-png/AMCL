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

public class ConsoleOutputHelper {
    public static final Ansi.Color TRACE = Ansi.Color.BLUE;
    public static final Ansi.Color DEBUG = Ansi.Color.CYAN;
    public static final Ansi.Color INFO = Ansi.Color.GREEN;
    public static final Ansi.Color WARN = Ansi.Color.YELLOW;
    public static final Ansi.Color ERROR = Ansi.Color.RED;
    public static final Ansi.Color FATAL = Ansi.Color.RED;
    public enum LogType {
        TRACE,
        DEBUG,
        INFO,
        WARN,
        ERROR,
        FATAL
    }
    public static class LogLine {
        private final LogType type;
        private final String content;
        public LogLine(LogType type, String content) {
            this.type = type;
            this.content = content;
        }
        public LogType getType() {
            return type;
        }
        public String getContent() {
            return content;
        }
    }
    private static LogType findLogType(String s, PrintStream stream) {
        if (stream == System.err){
            if (s.contains("/FATAL")) return LogType.FATAL;
            else if (s.contains("/ERROR") ) return LogType.ERROR;
            else if (s.startsWith("\tat")) return LogType.ERROR;
            else if (s.matches("([\\s\\S]*): ([\\s\\S]*)")) return LogType.ERROR;
            else if (s.startsWith("Caused by: ")) return LogType.ERROR;
            else return LogType.WARN;
        }
        else {
            if (s.contains("/TRACE")) return LogType.TRACE;
            else if (s.contains("/DEBUG")) return LogType.DEBUG;
            else if (s.contains("/INFO")) return LogType.INFO;
            else if (s.contains("/WARN")) return LogType.WARN;
            else if (s.contains("/ERROR")) return LogType.ERROR;
            else if (s.contains("\tat")) return LogType.ERROR;
            else if (s.matches("([\\s\\S]*): ([\\s\\S]*)")) return LogType.ERROR;
            else if (s.startsWith("Caused by: ")) return LogType.ERROR;
            else if (s.contains("/FATAL")) return LogType.FATAL;
            else return LogType.INFO;
        }
    }
    public static LogLine toLogLine(String content, PrintStream stream) {
        return new LogLine(findLogType(content, stream), content);
    }

    public static void printLog(String s, PrintStream stream){
        if (stream == System.err){
            if (s.contains("/FATAL")) printLogInternal(s, FATAL, stream);
            else if (s.contains("/ERROR") ) printLogInternal(s, ERROR, stream);
            else if (s.startsWith("\tat")) printLogInternal(s, ERROR, stream);
            else if (s.matches("([\\s\\S]*): ([\\s\\S]*)")) printLogInternal(s, ERROR, stream);
            else if (s.startsWith("Caused by: ")) printLogInternal(s, ERROR, stream);
            else printLogInternal(s, WARN, stream);
        }
        else {
            if (s.contains("/TRACE")) printLogInternal(s, TRACE, stream);
            else if (s.contains("/DEBUG")) printLogInternal(s, DEBUG, stream);
            else if (s.contains("/INFO")) printLogInternal(s, INFO, stream);
            else if (s.contains("/WARN")) printLogInternal(s, WARN, stream);
            else if (s.contains("/ERROR")) printLogInternal(s, ERROR, stream);
            else if (s.contains("\tat")) printLogInternal(s, ERROR, stream);
            else if (s.matches("([\\s\\S]*): ([\\s\\S]*)")) printLogInternal(s, ERROR, stream);
            else if (s.startsWith("Caused by: ")) printLogInternal(s, ERROR, stream);
            else if (s.contains("/FATAL")) printLogInternal(s, FATAL, stream);
            else printLogInternal(s, INFO, stream);
        }
    }

    private static void printLogInternal(String s, Ansi.Color color, PrintStream stream) {
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
                ConsoleOutputHelper.printLog(line, out);
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
