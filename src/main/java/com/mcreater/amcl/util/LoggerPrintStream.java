package com.mcreater.amcl.util;

import com.mcreater.amcl.game.MavenPathConverter;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.io.PrintStream;

import static org.fusesource.jansi.Ansi.ansi;

public class LoggerPrintStream extends PrintStream {
    public static LoggerPrintStream OUT = new LoggerPrintStream(System.out, StreamType.STDOUT);
    public static LoggerPrintStream ERR = new LoggerPrintStream(System.err, StreamType.STDERR);
    public enum StreamType {
        STDOUT,
        STDERR
    }
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
        if (jansiLoaded()) {
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
}
