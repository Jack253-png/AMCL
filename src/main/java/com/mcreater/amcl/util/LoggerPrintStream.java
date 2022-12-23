package com.mcreater.amcl.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.OutputStream;
import java.io.PrintStream;

import static org.fusesource.jansi.Ansi.ansi;

public class LoggerPrintStream extends PrintStream {
    public static LoggerPrintStream OUT = new LoggerPrintStream(System.out, StreamType.STDOUT);
    public static LoggerPrintStream ERR = new LoggerPrintStream(System.err, StreamType.STDERR);
    public static void setStdStream() {
        System.setErr(ERR);
        System.setOut(OUT);
    }
    public enum StreamType {
        STDOUT,
        STDERR
    }
    public static boolean enableJansi = true;
    StreamType type;
    public LoggerPrintStream(@NotNull OutputStream out, @NotNull StreamType type) {
        super(out);
        this.type = type;
    }
    private static boolean jansiLoaded() {
        try {
            Class.forName("org.fusesource.jansi.Ansi");
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
    private void jansiPrint(Object o, StreamType type) {
        PrintStream stream = (PrintStream) out;
        if (jansiLoaded() && enableJansi) {
            AnsiConsole.systemInstall();
            stream.print(ansi().fg(type == StreamType.STDERR ? Ansi.Color.RED : Ansi.Color.GREEN).a(o).reset());
            AnsiConsole.systemUninstall();
        }
        else {
            stream.print(o);
        }
    }

    public void print(boolean b) {
        jansiPrint(b, type);
    }

    public void print(char c) {
        jansiPrint(c, type);
    }

    public void print(int i) {
        jansiPrint(i, type);
    }

    public void print(long l) {
        jansiPrint(l, type);
    }

    public void print(float f) {
        jansiPrint(f, type);
    }

    public void print(double d) {
        jansiPrint(d, type);
    }

    public void print(@NotNull char[] s) {
        jansiPrint(s, type);
    }

    public void print(@Nullable String s) {
        jansiPrint(s, type);
    }

    public void print(@Nullable Object obj) {
        jansiPrint(obj, type);
    }

    private void jansiPrintln(Object obj, StreamType type) {
        jansiPrint(obj + "\n", type);
    }

    public void println(int x) {
        jansiPrintln(x, type);
    }

    public void println(char x) {
        jansiPrintln(x, type);
    }

    public void println(long x) {
        jansiPrintln(x, type);
    }

    public void println(float x) {
        jansiPrintln(x, type);
    }

    public void println(@NotNull char[] x) {
        jansiPrintln(x, type);
    }

    public void println(double x) {
        jansiPrintln(x, type);
    }

    public void println(@Nullable Object x) {
        jansiPrintln(x, type);
    }

    public void println(@Nullable String x) {
        jansiPrintln(x, type);
    }

    public void println(boolean x) {
        jansiPrintln(x, type);
    }

    public void println() {
        jansiPrintln("", type);
    }
}
