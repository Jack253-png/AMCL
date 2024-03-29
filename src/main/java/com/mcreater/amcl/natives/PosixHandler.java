package com.mcreater.amcl.natives;


import jnr.constants.platform.Errno;
import jnr.posix.POSIXHandler;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.util.Map;

public class PosixHandler implements POSIXHandler {
    private boolean verbose = false;

    @Override
    public void error(Errno error, String extraData) {
        System.err.printf("%s %s\n", error, extraData);
    }

    @Override
    public void error(Errno error, String methodName, String extraData) {
        System.err.printf("%s %s %s\n", error, methodName, extraData);
    }

    @Override
    public void unimplementedError(String methodName) {
        System.err.printf("%s\n", methodName);
    }

    @Override
    public void warn(WARNING_ID id, String message, Object... data) {
        System.err.printf("%s %s %s\n", id, message, data);
    }

    @Override
    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean value) {
        verbose = value;
    }

    @Override
    public File getCurrentWorkingDirectory() {
        return new File(System.getProperty("user.dir"));
    }

    @Override
    public String[] getEnv() {
        return System.getenv()
                .entrySet()
                .stream()
                .map(Map.Entry::toString)
                .toArray(String[]::new);
    }

    @Override
    public InputStream getInputStream() {
        return System.in;
    }

    @Override
    public PrintStream getOutputStream() {
        return System.out;
    }

    @Override
    public int getPID() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        System.out.println(name);
        return Integer.parseInt(name.split("@")[0]);
    }

    @Override
    public PrintStream getErrorStream() {
        return System.err;
    }
}

