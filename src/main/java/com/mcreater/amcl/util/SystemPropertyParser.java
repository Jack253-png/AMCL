package com.mcreater.amcl.util;

public class SystemPropertyParser {
    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(getString(key, "false"));
    }
    public static short getShort(String key) {
        return Short.parseShort(getString(key, "0"));
    }
    public static int getInt(String key) {
        return Integer.parseInt(getString(key, "0"));
    }
    public static long getLong(String key) {
        return Long.parseLong(getString(key, "0"));
    }
    public static byte getByte(String key) {
        return Byte.parseByte(getString(key, "0"));
    }
    public static String getString(String key, String d) {
        return System.getProperty(key, d);
    }
    public static String getString(String key) {
        return System.getProperty(key);
    }
    public static void set(String key, Object value) {
        System.setProperty(key, value.toString());
    }
}
