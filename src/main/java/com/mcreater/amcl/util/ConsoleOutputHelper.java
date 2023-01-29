package com.mcreater.amcl.util;

import com.mcreater.amcl.nativeInterface.OSInfo;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.fusesource.jansi.Ansi.ansi;

public class ConsoleOutputHelper {
    public static final Ansi.Color TRACE = Ansi.Color.BLUE;
    public static final Ansi.Color DEBUG = Ansi.Color.CYAN;
    public static final Ansi.Color INFO = Ansi.Color.GREEN;
    public static final Ansi.Color WARN = Ansi.Color.YELLOW;
    public static final Ansi.Color ERROR = Ansi.Color.RED;
    public static final Ansi.Color FATAL = Ansi.Color.RED;

    //    private static final Pattern LOG4J_STD = Pattern.compile("\\[(?<time>[\\s\\S]*)\\] \\[(?<thread>[\\s\\S]*)\\/(?<type>[\\s\\S]*)\\]\\: (?<message>[\\s\\S]*)");
    private static final Pattern LOG4J_STD = Pattern.compile("\\[(?<time>.*)] \\[(?<thread>.*)/(?<type>.*)]: (?<message>.*)");
    private static final Pattern EXC_STACK_START = Pattern.compile("(?<class>.*): (?<message>.*)");
    private static final Pattern EXC_STACK_CONTENT = Pattern.compile("\\tat (?<method>.*)\\((?<source>.*)\\)");
    private static final Pattern EXC_STACK_REDIRECT = Pattern.compile("Caused by: (?<class>.*): (?<message>.*)");
    private static final Pattern EXC_STACK_END = Pattern.compile("\t\\.\\.\\. (?<class>[0-9]*) more");

    public enum LogType {
        TRACE,
        DEBUG,
        INFO,
        WARN,
        ERROR,
        FATAL;

        public static LogType getType(String s) {
            switch (s) {
                case "TRACE":
                    return TRACE;
                case "DEBUG":
                    return DEBUG;
                default:
                case "INFO":
                    return INFO;
                case "WARN":
                    return WARN;
                case "ERROR":
                    return ERROR;
                case "FATAL":
                    return FATAL;
            }
        }
    }

    public static class LogLine {
        private final LogType type;
        private final String content;
        private final PrintStream stream;

        public LogLine(LogType type, String content, PrintStream stream) {
            this.type = type;
            this.content = content;
            this.stream = stream;
        }

        public LogType getType() {
            return type;
        }

        public String getContent() {
            return content;
        }

        public PrintStream getStream() {
            return stream;
        }

        public void printToStream() {
            AnsiConsole.systemInstall();
            stream.println(ansi().fg(toLogColor(type)).a(content).reset());
            AnsiConsole.systemUninstall();
        }
    }

    private static Ansi.Color toLogColor(LogType type) {
        switch (type) {
            case FATAL:
                return FATAL;
            case ERROR:
                return ERROR;
            case WARN:
                return WARN;
            default:
            case INFO:
                return INFO;
            case DEBUG:
                return DEBUG;
            case TRACE:
                return TRACE;
        }
    }

    private static LogType findLogType(String s, PrintStream stream) {
        Matcher log4jstd = LOG4J_STD.matcher(s);
        Matcher excstart = EXC_STACK_START.matcher(s);
        Matcher exccontent = EXC_STACK_CONTENT.matcher(s);
        Matcher excredirect = EXC_STACK_REDIRECT.matcher(s);
        Matcher excend = EXC_STACK_END.matcher(s);

        boolean excfind = excstart.find() || exccontent.find() || excredirect.find() || excend.find();
        if (stream != System.err) {
            if (log4jstd.find()) {
                try {
                    return LogType.getType(log4jstd.group("type"));
                } catch (Exception e) {
                    return LogType.INFO;
                }
            }
            return excfind ? LogType.ERROR : LogType.INFO;
        } else {
            return excfind ? LogType.ERROR : LogType.WARN;
        }
    }

    public static LogLine toLogLine(String content, PrintStream stream) {
        return new LogLine(findLogType(content, stream), content, stream);
    }

    public static void printLog(String s, PrintStream stream) {
        toLogLine(s, stream).printToStream();
    }

    private static void printLogInternal(String s, Ansi.Color color, PrintStream stream) {
        AnsiConsole.systemInstall();
        stream.println(ansi().fg(color).a(s).reset());
        AnsiConsole.systemUninstall();
    }

    public static String readStreamContent(InputStream inputStream, Charset charset) {
        StringBuilder f = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charset));
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

    public static void printStreamToPrintStream(InputStream stream, PrintStream out) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, OSInfo.NATIVE_CHARSET));
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
